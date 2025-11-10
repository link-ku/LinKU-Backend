package com.linku.backend.domain.alert.controller;

import com.linku.backend.domain.alert.dto.response.AlertListResponse;
import com.linku.backend.domain.alert.service.AlertService;
import com.linku.backend.domain.deapartmentConfig.dto.response.DepartmentConfigListResponse;
import com.linku.backend.domain.deapartmentConfig.service.DepartmentConfigService;
import com.linku.backend.global.response.BaseResponse;
import com.linku.backend.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;
    private final DepartmentConfigService departmentConfigService;

    @GetMapping("/my")
    public BaseResponse<AlertListResponse> getMyAlerts(
            @RequestParam(name = "department", required = false) List<String> departmentNames
    ) {
        if (departmentNames == null || departmentNames.isEmpty()) {
            return BaseResponse.of(ResponseCode.SUCCESS, alertService.getMyAlerts(1L));
        }
        return BaseResponse.of(ResponseCode.SUCCESS, alertService.getMyAlertsWithDepartments(1L, departmentNames));
    }

    @GetMapping("/subscription")
    public BaseResponse<DepartmentConfigListResponse> getAllSubscription(){
        DepartmentConfigListResponse response = departmentConfigService.getAllDepartmentConfigs();
        return BaseResponse.of(ResponseCode.SUCCESS, response);
    }

    @GetMapping("/subscription/my")
    public BaseResponse<DepartmentConfigListResponse> getAllMySubscription(){
        DepartmentConfigListResponse response = departmentConfigService.getAllMyDepartmentConfigs(1L);
        return BaseResponse.of(ResponseCode.SUCCESS, response);
    }

    @PostMapping("/{departmentId}")
    public BaseResponse<Void> subscribeDepartment(@PathVariable Long departmentId){
        departmentConfigService.subscribeDepartment(1L, departmentId);
        return BaseResponse.of(ResponseCode.SUCCESS, null);
    }

    @DeleteMapping("/{departmentId}")
    public BaseResponse<Void> deleteSubscription(@PathVariable Long departmentId){
        departmentConfigService.deleteSubscription(1L, departmentId);
        return BaseResponse.of(ResponseCode.SUCCESS, null);
    }
}
