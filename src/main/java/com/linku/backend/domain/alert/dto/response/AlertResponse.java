package com.linku.backend.domain.alert.dto.response;

import com.linku.backend.domain.alert.Alert;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AlertResponse {
    private Long alertId;
    private String departmentName;
    private String url;
    private String title;
    private LocalDateTime postTime;
    private String content;

    public static AlertResponse from(Alert alert) {
        return AlertResponse.builder()
                .alertId(alert.getId())
                .departmentName(alert.getDepartmentConfig().getName())
                .url(alert.getUrl())
                .title(alert.getTitle())
                .postTime(alert.getPostTime())
                .content(alert.getContent())
                .build();
    }
}
