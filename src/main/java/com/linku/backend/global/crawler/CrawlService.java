package com.linku.backend.global.crawler;

import com.linku.backend.domain.alert.Alert;
import com.linku.backend.domain.alert.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlService {
    private final List<DepartmentConfig> configs;
    private final AlertService alertService;
    private final AlertParserFactory parserFactory;

    public void crawlAll() {
        for (DepartmentConfig config : configs) {
            try {
                // 팩토리를 통해 파서 객체를 가져옴
                AlertParser parser = parserFactory.getParser(config);
                List<Alert> parsed = parser.parse(config);

                for (Alert alert : parsed) {
                    if (alertService.isNew(alert)) {
                        alertService.save(alert);
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
}
