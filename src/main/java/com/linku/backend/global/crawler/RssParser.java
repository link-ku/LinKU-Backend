package com.linku.backend.global.crawler;

import com.linku.backend.domain.alert.Alert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RssParser implements AlertParser {
    @Override
    public List<Alert> parse(DepartmentConfig config) throws IOException {
        Document doc = Jsoup.connect(config.getUrl()).get();
        Elements items = doc.select("item");
        List<Alert> alerts = new ArrayList<>();

        for (Element item : items) {
            Alert alert = new Alert();
            alert.setTitle(item.select("title").text());
            alert.setUrl(item.select("link").text());
            alert.setDate(LocalDateTime.now()); // RSS 'pubDate'를 파싱하여 사용 가능
            alert.setDepartment(config.getName());
            alerts.add(alert);
        }
        return alerts;
    }
}

