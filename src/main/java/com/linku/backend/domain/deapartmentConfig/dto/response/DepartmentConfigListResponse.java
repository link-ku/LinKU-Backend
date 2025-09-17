package com.linku.backend.domain.deapartmentConfig.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DepartmentConfigListResponse {
    private List<DepartmentConfigResponse> departmentConfigList;

    public static DepartmentConfigListResponse from(List<DepartmentConfigResponse> list) {
        return DepartmentConfigListResponse.builder()
                .departmentConfigList(list == null ? List.of() : List.copyOf(list))
                .build();
    }
}
