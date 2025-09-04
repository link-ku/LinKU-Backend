package com.linku.backend.global.crawler;

import com.linku.backend.domain.alert.Alert;

import java.util.List;

public class CrawlerService {
    private List<DepartmentConfig> configs;
    private AlertService noticeService;
    private NotificationService notificationService;

    public void crawlAll() {
        for (DepartmentConfig config : configs) {
            AlertParser parser = config.isRss() ? new RssParser() : new HtmlParser();
            List<Alert> newNotices = parser.parse(config);
            for (Alert notice : newNotices) {
                if (noticeService.isNew(notice)) {
                    noticeService.save(notice);
                    notificationService.notifyUsers(notice);
                }
            }
        }
    }
}
