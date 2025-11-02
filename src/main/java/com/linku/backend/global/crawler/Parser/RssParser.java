package com.linku.backend.global.crawler.Parser;

import com.linku.backend.domain.alert.Alert;
import com.linku.backend.domain.common.enums.Status;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import org.springframework.stereotype.Component;

@Component
public class RssParser implements AlertParser {
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    // 숫자 포맷
    private static final DateTimeFormatter NUMERIC_PUBDATE =
            new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd HH:mm:ss")
                    .optionalStart()
                    .appendLiteral('.')
                    .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, false)
                    .optionalEnd()
                    .toFormatter(Locale.KOREAN);

    @Override
    public List<Alert> parse(DepartmentConfig config) throws IOException {
        Document doc = Jsoup.connect(config.getUrl())
                .userAgent("LinkU-Crawler/1.0 (+https://linku.app)")
                .timeout(10_000)
                .parser(Parser.xmlParser())
                .get();

        Elements items = doc.select("channel > item");
        String origin = originOf(config.getUrl());

        return items.stream().map(item -> {
                    // 1. 제목 추출 및 필수 검사
                    String title = item.selectFirst("title") != null ? item.selectFirst("title").text().trim() : "";
                    if (title.isBlank()) {
                        return null; // 제목 없으면 Alert 생성 건너뛰기
                    }

                    // 2. 링크 추출 및 필수 검사
                    String link = item.selectFirst("link") != null ? item.selectFirst("link").text().trim() : "";
                    if (link.isBlank()) {
                        return null; // 링크 없으면 Alert 생성 건너뛰기
                    }

                    Alert a = new Alert();
                    a.setTitle(title);
                    a.setUrl(toAbsolute(origin, link)); // 상대 → 절대 URL

                    String pub = item.selectFirst("pubDate") != null ? item.selectFirst("pubDate").text() : "";
                    a.setPostTime(parsePubDate(pub));   // 견고한 파서

                    String content = item.selectFirst("description") != null ? item.selectFirst("description").text() : "";
                    a.setContent(content);
                    a.setStatus(Status.ACTIVE);

                    return a;

                })
                .filter(java.util.Objects::nonNull) // null 객체(제목 또는 링크가 없는 항목)는 최종 목록에서 제외
                .collect(Collectors.toList());
    }

    // Rss를 사용한 원본 링크를 받음
    private static String originOf(String url) {
        try {
            URL u = new URL(url);
            String port = (u.getPort() == -1) ? "" : ":" + u.getPort();
            return u.getProtocol() + "://" + u.getHost() + port;
        } catch (MalformedURLException e) {
            return "";
        }
    }

    // 절대 링크로 바꿔줌
    private static String toAbsolute(String origin, String link) {
        if (link == null || link.isBlank()) return link;
        if (link.startsWith("http://") || link.startsWith("https://")) return link;
        if (!origin.isEmpty()) return origin + (link.startsWith("/") ? link : ("/" + link));
        return link;
    }

    private static LocalDateTime parsePubDate(String s) {
        if (s == null || s.isBlank()) return LocalDateTime.now(ZONE);

        // 숫자 포맷 먼저 시도
        try {
            return LocalDateTime.parse(s, NUMERIC_PUBDATE);
        } catch (Exception ignore) { }

        // 실패 시 now
        return LocalDateTime.now(ZONE);
    }
}

