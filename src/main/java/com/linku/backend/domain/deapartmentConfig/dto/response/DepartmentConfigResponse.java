package com.linku.backend.domain.deapartmentConfig.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepartmentConfigResponse {
    private Long departmentConfigId;
    private String departmentConfigName;

    public static DepartmentConfigResponse of(Long departmentConfigId, String departmentConfigName){
        return DepartmentConfigResponse.builder()
                .departmentConfigId(departmentConfigId)
                .departmentConfigName(departmentConfigName)
                .build();
    }
}
