package com.linku.backend.global.crawler;

import com.linku.backend.domain.alert.service.AlertService;
import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import com.linku.backend.domain.deapartmentConfig.repository.DepartmentConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlService {
    private final List<DepartmentConfig> configs;
    private final AlertService alertService;
    private final AlertParserFactory parserFactory;
    private final WebClient.Builder webClientBuilder;
    private final DepartmentConfigRepository departmentConfigRepository;

    //    public void crawlAll() {
//        for (DepartmentConfig config : configs) {
//            try {
//                // 팩토리를 통해 파서 객체를 가져옴
//                AlertParser parser = parserFactory.getParser(config);
//                List<Alert> parsed = parser.parse(config);
//
//                for (Alert alert : parsed) {
//                    if (alertService.isNew(alert)) {
//                        alertService.save(alert);
//                    }
//                }
//            } catch (IOException e) {
//                log.warn("크롤 실패: dept={}, url={}, msg={}", config.getName(), config.getUrl(), e.getMessage());
//            } catch (Exception e) {
//                log.error("예상치 못한 오류: dept={}, url={}", config.getName(), config.getUrl(), e);
//            }
//        }
//    }

    // 실행 직후 1회
    @PostConstruct
    public void init() {
        crawlAll();
    }

    // 오전 3시마다 작동
    @Scheduled(cron = "0 0 3 * * *")
    public void crawlAll() {
        log.info("크롤링 시작!");

        // JPA findAll() = Blocking → defer + boundedElastic
        Flux.defer(() -> Flux.fromIterable(departmentConfigRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::crawlAndSave) // Mono<Void>
                .sequential()
                .subscribe(
                        null,
                        e -> log.error("비동기 크롤링 중 오류 발생", e),
                        () -> log.info("크롤링 완료!")
                );
    }

    private Mono<Void> crawlAndSave(DepartmentConfig config) {
        WebClient webClient = webClientBuilder.baseUrl(config.getUrl()).build();

        return webClient.head()
                .retrieve()
                .toBodilessEntity()
                .flatMap(response -> {
                    String lastModified = response.getHeaders().getFirst("Last-Modified");

                    // 변경 없으면 크로링 작동 안함
                    if (lastModified != null && lastModified.equals(config.getLastModified())) {
                        log.info("변경 없음: dept={}, url={}", config.getName(), config.getUrl());
                        return Mono.empty();
                    }

                    // 실제 GET 요청
                    return webClient.get()
                            .retrieve()
                            .bodyToMono(String.class)
                            .onErrorResume(e -> {
                                log.warn("크롤 실패: dept={}, url={}, msg={}", config.getName(), config.getUrl(), e.getMessage());
                                return Mono.empty();
                            })
                            // 파싱 (IOException 등 체크예외 안전 처리 + 블로킹 오프로딩)
                            .flatMap(html -> Mono.fromCallable(() -> {
                                                AlertParser parser = parserFactory.getParser(config);
                                                return parser.parse(config); // List<Alert>, throws IOException
                                            })
                                            .subscribeOn(Schedulers.boundedElastic())
                                            .onErrorResume(IOException.class, e -> {
                                                log.warn("파싱 실패(체크예외 처리): dept={}, url={}, msg={}", config.getName(), config.getUrl(), e.getMessage());
                                                return Mono.just(List.of());
                                            })
                            )
                            .flatMapMany(Flux::fromIterable)
                            // isNew가 블로킹(JPA)이므로 오프로딩
                                    .filterWhen(alert -> Mono.fromCallable(() -> alertService.isNew(alert))
                                    .subscribeOn(Schedulers.boundedElastic()))
                            // save도 블로킹(JPA)이므로 오프로딩
                            .flatMap(alert -> Mono.fromCallable(() -> alertService.save(alert))
                                    .subscribeOn(Schedulers.boundedElastic()))
                            .collectList()
                            .flatMap(savedAlerts -> {
                                log.info("새로운 알림 {}개 저장 완료: dept={}", savedAlerts.size(), config.getName());
                                // lastModified 업데이트 저장 (JPA save 블로킹 → 오프로딩)
                                return Mono.fromCallable(() -> {
                                            config.setLastModified(lastModified);
                                            return departmentConfigRepository.save(config);
                                        })
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .then();
                            });
                })
                .onErrorResume(e -> {
                    log.warn("변경 확인 실패: dept={}, url={}, msg={}", config.getName(), config.getUrl(), e.getMessage());
                    return Mono.empty();
                });
    }
}
