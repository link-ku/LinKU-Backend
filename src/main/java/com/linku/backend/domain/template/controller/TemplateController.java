package com.linku.backend.domain.template.controller;

import com.linku.backend.domain.template.dto.request.TemplateCreateRequest;
import com.linku.backend.domain.template.dto.request.TemplateUpdateRequest;
import com.linku.backend.domain.template.dto.response.TemplateListResponse;
import com.linku.backend.domain.template.dto.response.TemplateResponse;
import com.linku.backend.domain.template.service.TemplateService;
import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateResponse;
import com.linku.backend.global.resolver.CurrentUserId;
import com.linku.backend.global.response.BaseResponse;
import com.linku.backend.global.response.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping
    public BaseResponse<TemplateResponse> createTemplate(
            @CurrentUserId Long userId,
            @RequestBody @Valid TemplateCreateRequest request
    ) {
        TemplateResponse response = templateService.createTemplate(userId, request);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                response
        );
    }

    @GetMapping("/owned")
    public BaseResponse<List<TemplateListResponse>> getOwnedTemplates(
            @CurrentUserId Long userId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String query
    ) {
        System.out.println("ID: "+userId);
        List<TemplateListResponse> response = templateService.getOwnedTemplates(userId, sort, query);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                response
        );
    }

    @GetMapping("/cloned")
    public BaseResponse<List<TemplateListResponse>> getClonedTemplates(
            @CurrentUserId Long userId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String query
    ) {
        List<TemplateListResponse> response = templateService.getClonedTemplates(userId, sort, query);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                response
        );
    }

    @GetMapping("/{templateId}")
    public BaseResponse<TemplateResponse> getTemplateDetail(
            @CurrentUserId Long userId,
            @PathVariable Long templateId
    ) {
        TemplateResponse response = templateService.getTemplateDetail(userId, templateId);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                response
        );
    }

    @PutMapping("/{templateId}")
    public BaseResponse<TemplateResponse> updateTemplate(
            @CurrentUserId Long userId,
            @PathVariable Long templateId,
            @RequestBody @Valid TemplateUpdateRequest request
    ) {
        TemplateResponse response = templateService.updateTemplate(userId, templateId, request);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                response
        );
    }

    @DeleteMapping("/{templateId}")
    public BaseResponse<Void> deleteTemplate(
            @CurrentUserId Long userId,
            @PathVariable Long templateId
    ) {
        templateService.deleteTemplate(userId, templateId);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                null
        );
    }

    @PostMapping("/{templateId}/post")
    public BaseResponse<PostedTemplateResponse> postTemplate(
            @CurrentUserId Long userId,
            @PathVariable Long templateId
    ) {
        PostedTemplateResponse response = templateService.postTemplate(userId, templateId);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                response
        );
    }
}
