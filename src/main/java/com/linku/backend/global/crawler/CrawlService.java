package com.linku.backend.global.crawler;

import com.linku.backend.domain.alert.Alert;
import com.linku.backend.domain.alert.service.AlertService;
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
    // 매일 오전 3시에 실행
    @Scheduled(cron = "0 0 3 * * *")
    public void crawlAll() {
        log.info("크롤링 시작!");

        // DB에서 모든 크롤링 설정 정보를 가져와 비동기 스트림(Flux)으로 변환
        Flux.fromIterable(departmentRepository.findAll())
                .parallel() // 병렬 처리 시작
                .runOn(Schedulers.parallel()) // 병렬 스케줄러에서 실행
                .flatMap(this::crawlAndSave) // 각 설정에 대해 크롤링 및 저장
                .sequential() // 순차적으로 결과 결합
                .subscribe(
                        null, // 결과 처리 (성공) - 필요시 로직 추가
                        e -> log.error("비동기 크롤링 중 오류 발생", e), // 에러 처리
                        () -> log.info("크롤링 완료!") // 모든 작업 완료 시
                );
    }

    // WebClient를 사용하여 비동기적으로 페이지를 크롤링하고 파싱하여 저장하는 메서드
    private Mono<Void> crawlAndSave(DepartmentConfig config) {
        WebClient webClient = webClientBuilder.baseUrl(config.getUrl()).build();

        return webClient.head() // HEAD 요청으로 헤더 정보만 가져옴
                .retrieve()
                .toBodilessEntity() // 본문 없이 헤더만 받음
                .flatMap(response -> {
                    String lastModified = response.getHeaders().getFirst("Last-Modified");

                    // 최종 수정 시간이 저장된 값과 같으면 크롤링하지 않고 종료
                    if (lastModified != null && lastModified.equals(config.getLastModified())) {
                        log.info("변경 없음: dept={}, url={}", config.getName(), config.getUrl());
                        return Mono.empty();
                    }

                    // 최종 수정 시간이 변경되었으면, 실제 GET 요청을 보내 크롤링 진행
                    AlertParser parser = parserFactory.getParser(config);
                    return webClient.get()
                            .retrieve()
                            .bodyToMono(String.class)
                            .onErrorResume(e -> {
                                log.warn("크롤 실패: dept={}, url={}, msg={}", config.getName(), config.getUrl(), e.getMessage());
                                return Mono.empty();
                            })
                            .flatMapMany(html -> Flux.fromIterable(parser.parse(config)))
                            .filter(alertService::isNew)
                            .flatMap(alertService::save)
                            .doOnComplete(() -> {
                                // 크롤링 및 저장 완료 후, 최종 수정 시간 업데이트
                                config.setLastModified(lastModified);
                                configRepository.save(config).block(); // 동기적으로 저장
                            })
                            .then();
                })
                .onErrorResume(e -> {
                    log.warn("변경 확인 실패: dept={}, url={}, msg={}", config.getName(), config.getUrl(), e.getMessage());
                    return Mono.empty();
                });
    }
}
