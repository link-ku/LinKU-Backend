package com.linku.backend.domain.alert.controller;

import com.linku.backend.domain.alert.dto.response.AlertListResponse;
import com.linku.backend.domain.alert.dto.response.AlertResponse;
import com.linku.backend.domain.alert.service.AlertService;
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

    @GetMapping("/my")
    public BaseResponse<AlertListResponse> getMyAlerts(
    ) {
        AlertListResponse response = alertService.getMyAlerts();

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                response
        );
    }

    @PostMapping("/subscribe")
    public BaseResponse<Void> subscribeDepartment(){
        alertService.subscribeDepartment();
        return BaseResponse.of(ResponseCode.SUCCESS, null);
    }

    @DeleteMapping("/{departmentId}")
    public BaseResponse<Void> deleteSubscription(){
        alertService.deleteSubscription();
        return BaseResponse.of(ResponseCode.SUCCESS, null);
    }

    @PatchMapping("/{departmentId}")
    public BaseResponse<Void> updateSubscription(){
        alertService.updateSubscription();
        return BaseResponse.of(ResponseCode.SUCCESS, null);
    }
}
