package com.linku.backend.domain.postedtemplate.controller;

import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateListResponse;
import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateResponse;
import com.linku.backend.domain.postedtemplate.service.PostedTemplateService;
import com.linku.backend.domain.template.dto.response.TemplateResponse;
import com.linku.backend.global.resolver.CurrentUserId;
import com.linku.backend.global.response.BaseResponse;
import com.linku.backend.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posted-templates")
public class PostedTemplateController {

    private final PostedTemplateService postedTemplateService;

    @GetMapping("/my")
    public BaseResponse<List<PostedTemplateListResponse>> getMyPostedTemplates(
            @CurrentUserId Long userId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String query
    ) {
        List<PostedTemplateListResponse> response = postedTemplateService.getMyPostedTemplates(userId, sort, query);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                response
        );
    }

    @GetMapping("/public")
    public BaseResponse<List<PostedTemplateListResponse>> getPublicPostedTemplates(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String query
    ) {
        List<PostedTemplateListResponse> response = postedTemplateService.getPublicPostedTemplates(sort, query);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                response
        );
    }

    @GetMapping("/{postedTemplateId}")
    public BaseResponse<PostedTemplateResponse> getPostedTemplateDetail(
            @CurrentUserId Long userId,
            @PathVariable Long postedTemplateId
    ) {
        PostedTemplateResponse response = postedTemplateService.getPostedTemplateDetail(postedTemplateId);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                response
        );
    }

    @DeleteMapping("/{postedTemplateId}")
    public BaseResponse<Void> deletePostedTemplate(
            @CurrentUserId Long userId,
            @PathVariable Long postedTemplateId
    ) {
        postedTemplateService.deletePostedTemplate(userId, postedTemplateId);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                null
        );
    }

    @PostMapping("/{postedTemplateId}/clone")
    public BaseResponse<TemplateResponse> clonePostedTemplate(
            @CurrentUserId Long userId,
            @PathVariable Long postedTemplateId
    ) {
        TemplateResponse response = postedTemplateService.clonePostedTemplate(userId, postedTemplateId);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                response
        );
    }
}
