package com.linku.backend.domain.deapartmentConfig.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public class DepartmentConfigListResponse {
    List<DepartmentConfigResponse> departmentConfigList;

    public static DepartmentConfigListResponse from(List<DepartmentConfigResponse> list) {
        return DepartmentConfigListResponse.builder()
                .departmentConfigList(list == null ? List.of() : List.copyOf(list))
                .build();
    }
}
