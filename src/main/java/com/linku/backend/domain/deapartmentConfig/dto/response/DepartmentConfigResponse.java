package com.linku.backend.domain.deapartmentConfig.dto.response;

import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import lombok.Builder;

@Builder
public class DepartmentConfigResponse {
    Long departmentConfigId;
    String departmentConfigName;

    public static DepartmentConfigResponse of(Long departmentConfigId,  String departmentConfigName){
        return DepartmentConfigResponse.builder()
                .departmentConfigId(departmentConfigId)
                .departmentConfigName(departmentConfigName)
                .build();
    }
}
