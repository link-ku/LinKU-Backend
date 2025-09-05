package com.linku.backend.global.crawler;

import com.example.crawler.config.DepartmentConfig;
import com.example.crawler.domain.Alert;
import com.example.crawler.parser.AlertParser;
import com.example.crawler.parser.HtmlParser;
import com.example.crawler.parser.RssParser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrawlService {
    private static final Logger log = LoggerFactory.getLogger(CrawlService.class);

    private final List<DepartmentConfig> configs;
    private final AlertService alertService;
    private final NotificationService notificationService;

    public void crawlAll() {
        for (DepartmentConfig config : configs) {
            try {
                AlertParser parser = config.isRss() ? new RssParser() : new HtmlParser();
                List<Alert> parsed = parser.parse(config);

                for (Alert alert : parsed) {
                    if (alertService.isNew(alert)) {
                        alertService.save(alert);
                        notificationService.notifyUsers(alert);
                    }
                }
            } catch (IOException e) {
                log.warn("크롤 실패: dept={}, url={}, msg={}", config.getName(), config.getUrl(), e.getMessage());
            } catch (Exception e) {
                log.error("예상치 못한 오류: dept={}, url={}", config.getName(), config.getUrl(), e);
            }
        }
    }
}
