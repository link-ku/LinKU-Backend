package com.linku.backend.global.crawler;

import org.springframework.stereotype.Component;

@Component
public class AlertParserFactory {
    // 재사용이 가능한 싱글톤 기법 적용
    private final RssParser rssParser = new RssParser();
    private final HtmlParser htmlParser = new HtmlParser();

    public AlertParser getParser(DepartmentConfig config) {
        if (config.isRss()) {
            return rssParser;
        } else {
            return htmlParser;
        }
    }
}
