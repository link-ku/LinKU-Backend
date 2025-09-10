package com.linku.backend.domain.alert.controller;

import com.linku.backend.domain.alert.dto.response.AlertListResponse;
import com.linku.backend.domain.alert.service.AlertService;
import com.linku.backend.domain.template.dto.request.TemplateCreateRequest;
import com.linku.backend.domain.template.dto.response.TemplateResponse;
import com.linku.backend.global.response.BaseResponse;
import com.linku.backend.global.response.ResponseCode;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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





}
