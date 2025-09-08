package com.linku.backend.global.crawler;

import com.linku.backend.domain.alert.Alert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;

public class RssParser implements AlertParser {
    @Override
    public List<Alert> parse(DepartmentConfig config) throws IOException {
        Document doc = Jsoup.connect(config.getUrl()).get();
        Elements items = doc.select("item");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

        return items.stream()
                .map(item -> {
                    Alert alert = new Alert();
                    alert.setTitle(item.select("title").text());
                    alert.setUrl(item.select("link").text());

                    String pubDateStr = item.select("pubDate").text();
                    if (!pubDateStr.isEmpty()) {
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(pubDateStr, formatter);
                        alert.setPostTime(zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
                    } else {
                        alert.setPostTime(LocalDateTime.now());
                    }
                    return alert;
                })
                .collect(Collectors.toList());
    }
}

