package com.linku.backend.global.crawler.Parser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;

@RequiredArgsConstructor
@Component
public class AlertParserFactory {
    // 재사용이 가능한 싱글톤 기법 적용
    private final RssParser rssParser;
    private final HtmlParser htmlParser;

    public AlertParser getParser(DepartmentConfig config) {
        if (config.isRss()) {
            return rssParser;
        } else {
            return htmlParser;
        }
    }
}
