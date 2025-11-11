package com.linku.backend.domain.alert.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AlertListResponse {

    private List<AlertResponse> alertResponseList;

    public static AlertListResponse from(List<AlertResponse> list) {
        return AlertListResponse.builder()
                .alertResponseList(list == null ? List.of() : List.copyOf(list))
                .build();
    }
}
