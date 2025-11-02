package com.linku.backend.global.crawler.Parser;

import com.linku.backend.domain.alert.Alert;
import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 일반적인 HTML 페이지에서 게시글 목록을 크롤링하기 위한 AlertParser 구현체입니다.
 * DepartmentConfig에 설정된 CSS 선택자를 사용하여 게시글 항목을 추출합니다.
 */
@Component
public class HtmlParser implements AlertParser {
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    // RssParser와 동일하게 숫자 형식의 날짜를 처리하기 위한 포맷터
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
        // Jsoup으로 HTML 문서를 가져옴 (XML 파서가 아닌 기본 HTML 파서 사용)
        Document doc = Jsoup.connect(config.getUrl())
                .userAgent("LinkU-Crawler/1.0 (+https://linku.app)")
                .timeout(10_000)
                .get(); // 기본적으로 HTML 파서를 사용

        // 1. config.getListSelector()를 사용하여 게시글 목록 요소들을 선택 (<tr> 또는 <li>)
        // **수정**: 반복문이 정상적으로 돌 수 있도록 다시 config.getListSelector()를 사용합니다.
        Elements items = doc.select("table.board-table tr");

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        String origin = originOf(config.getUrl());

        // 2. 각 게시글 요소를 Alert 객체로 매핑 (item은 이제 게시글 행 전체를 의미합니다.)
        return items.stream().map(item -> {
            Alert a = new Alert();

            // 2-1 & 2-2: 링크와 제목을 하드코딩된 CSS 선택자 "td.td-subject a"를 기준으로 동시에 추출
            // 실제 게시글 HTML 구조: <td class="td-subject"><a href="..."><strong>제목</strong></a></td>
            Element linkEl = item.selectFirst("td.td-subject a");

            if (linkEl != null) {
                // URL 추출: <a> 태그의 href 속성
                String link = linkEl.attr("href");
                a.setUrl(toAbsolute(origin, link));

                // 제목 추출: <a> 태그 내부에 있는 <strong> 태그의 텍스트를 사용
                Element titleInner = linkEl.selectFirst("strong");
                String title = (titleInner != null)
                        ? titleInner.text().trim()
                        : linkEl.text().trim(); // <strong>이 없을 경우 <a> 태그 텍스트 사용
                a.setTitle(title.isBlank() ? "제목 없음" : title);

            } else {
                // 링크 요소(<a>)가 없는 경우 예외 처리
                a.setUrl("");

                // <a>가 없을 경우, 제목 필드 자체(td.td-subject)를 기준으로 제목 추출 시도 (하드코딩)
                Element titleEl = item.selectFirst("td.td-subject");
                a.setTitle(titleEl != null ? titleEl.text().trim() : "제목 없음");
            }

            // 2-3. PostTime: 날짜를 하드코딩된 CSS 선택자 "td.td-date"를 사용하여 추출 및 파싱
            Element dateEl = item.selectFirst("td.td-date");
            String pub = dateEl != null ? dateEl.text().trim() : "";
            a.setPostTime(parsePubDate(pub));

            // HTML 목록 크롤링의 경우 상세 내용은 비워둠 (별도 요청이 없는 한)
            a.setContent("");

            return a;
        }).collect(Collectors.toList());
    }

    /**
     * URL에서 기본 도메인(Origin)을 추출합니다.
     */
    private static String originOf(String url) {
        try {
            URL u = new URL(url);
            String port = (u.getPort() == -1) ? "" : ":" + u.getPort();
            return u.getProtocol() + "://" + u.getHost() + port;
        } catch (MalformedURLException e) {
            return "";
        }
    }

    /**
     * 상대 링크를 절대 링크로 변환합니다.
     */
    private static String toAbsolute(String origin, String link) {
        if (link == null || link.isBlank()) return link;
        // 이미 절대 URL인 경우
        if (link.startsWith("http://") || link.startsWith("https://")) return link;
        // 상대 URL인 경우 Origin에 붙여서 절대 URL로 만듦
        if (!origin.isEmpty()) return origin + (link.startsWith("/") ? link : ("/" + link));
        return link;
    }

    /**
     * 날짜 문자열을 LocalDateTime으로 파싱합니다.
     */
    private static LocalDateTime parsePubDate(String s) {
        if (s == null || s.isBlank()) return LocalDateTime.now(ZONE);

        // 1. 숫자 포맷 시도 (yyyy-MM-dd HH:mm:ss)
        try {
            return LocalDateTime.parse(s, NUMERIC_PUBDATE);
        } catch (Exception ignore) { }

        // 2. 일반적인 한국 날짜 포맷 시도 (예: yyyy.MM.dd)
        try {
            DateTimeFormatter KOREAN_DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd").withZone(ZONE);
            return LocalDateTime.parse(s, KOREAN_DATE);
        } catch (Exception ignore) { }

        // 3. 실패 시 현재 시간 반환
        return LocalDateTime.now(ZONE);
    }
}