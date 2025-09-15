package com.linku.backend.domain.alert.controller;

import com.linku.backend.domain.alert.dto.response.AlertListResponse;
import com.linku.backend.domain.alert.dto.response.AlertResponse;
import com.linku.backend.domain.alert.service.AlertService;
import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import com.linku.backend.domain.template.dto.request.TemplateCreateRequest;
import com.linku.backend.domain.template.dto.response.TemplateResponse;
import com.linku.backend.global.response.BaseResponse;
import com.linku.backend.global.response.ResponseCode;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;
    private final DepartmentConfigService departmentConfigService;

    @GetMapping("/my")
    public BaseResponse<AlertListResponse> getMyAlerts(
    ) {
        AlertListResponse response = alertService.getMyAlerts();

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                response
        );
    }

//    @GetMapping("/subscription")
//    public BaseResponse<AlertListResponse> getSubscriptionAlerts(
//            departmentConfigService
//    )

    @PostMapping("/{departmentId}")
    public BaseResponse<Void> subscribeDepartment(@PathVariable Long departmentId){
        alertService.subscribeDepartment(departmentId);
        return BaseResponse.of(ResponseCode.SUCCESS, null);
    }

    @DeleteMapping("/{departmentId}")
    public BaseResponse<Void> deleteSubscription(@PathVariable Long departmentId){
        alertService.deleteSubscription(departmentId);
        return BaseResponse.of(ResponseCode.SUCCESS, null);
    }
}
