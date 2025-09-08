package com.linku.backend.global.crawler;

import com.linku.backend.domain.alert.service.AlertService;
import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import com.linku.backend.domain.deapartmentConfig.repository.DepartmentConfigRepository;
import com.linku.backend.global.exception.LinkuException;
import com.linku.backend.global.response.ResponseCode;
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
    private final AlertService alertService;
    private final AlertParserFactory parserFactory;
    private final WebClient.Builder webClientBuilder;
    private final DepartmentConfigRepository departmentConfigRepository;

    // 실행 직후 1회
    @PostConstruct
    public void init() {
        crawlAll();
    }

    // 오전 3시마다 작동
    @Scheduled(cron = "0 0 3 * * *")
    public void crawlAll() {
        log.info("크롤링 시작!");
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
        WebClient client = webClientBuilder.build();

        // 2) 조건부 GET으로 바꾸고 304 처리
        return client.get()
                .uri(config.getUrl())
                .headers(h -> {
                    h.set("User-Agent", "LinkU-Crawler/1.0 (+https://linku.app)");
                    if (config.getLastModified() != null && !config.getLastModified().isBlank()) {
                        h.set("If-Modified-Since", config.getLastModified());
                    }
                })
                .exchangeToMono(resp -> {
                    if (resp.statusCode().value() == 304) {
                        log.info("변경 없음(304): dept={}, url={}", config.getName(), config.getUrl());
                        return Mono.empty();
                    }
                    if (resp.statusCode().isError()) {
                        return resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> Mono.error(LinkuException.of(ResponseCode.CRAWLING_FAILED)));
                    }
                    return resp.toEntity(String.class);
                })
                .flatMap(entity -> {
                    String lastModified = entity.getHeaders().getFirst("Last-Modified");
                    String body = entity.getBody();
                    if (body == null || body.isBlank()) return Mono.empty();

                    return Mono.fromCallable(() -> {
                                AlertParser parser = parserFactory.getParser(config);
                                return parser.parse(config);
                            })
                            .subscribeOn(Schedulers.boundedElastic())
                            .onErrorMap(IOException.class, e -> LinkuException.of(ResponseCode.CRAWLING_PARSING_FAILED))
                            .flatMapMany(Flux::fromIterable)
                            .filterWhen(a -> Mono.fromCallable(() -> alertService.isNew(a))
                                    .subscribeOn(Schedulers.boundedElastic()))
                            .flatMap(a -> Mono.fromCallable(() -> alertService.saveWithDept(a, config.getId()))
                                    .subscribeOn(Schedulers.boundedElastic()))
                            .collectList()
                            .flatMap(saved -> {
                                log.info("새 알림 {}개 저장: dept={}", saved.size(), config.getName());
                                // 각 학과마다 마지막 수정 날짜 변경
                                return Mono.fromCallable(() -> {
                                            config.setLastModified(lastModified);
                                            return departmentConfigRepository.save(config);
                                        })
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .then();
                            });
                })
                .onErrorResume(LinkuException.class, e -> {
                    log.warn("크롤링 실패: dept={}, url={}, msg={}", config.getName(), config.getUrl(), e.getMessage());
                    return Mono.empty();
                });
    }
}
