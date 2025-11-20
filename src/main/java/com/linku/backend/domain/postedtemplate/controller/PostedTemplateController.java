package com.linku.backend.domain.postedtemplate.controller;

import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateListResponse;
import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateResponse;
import com.linku.backend.domain.postedtemplate.service.PostedTemplateService;
import com.linku.backend.domain.template.dto.response.TemplateResponse;
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
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String query
    ) {
        List<PostedTemplateListResponse> response = postedTemplateService.getMyPostedTemplates(sort, query);

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
            @PathVariable Long postedTemplateId
    ) {
        postedTemplateService.deletePostedTemplate(postedTemplateId);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                null
        );
    }

    @PostMapping("/{postedTemplateId}/clone")
    public BaseResponse<TemplateResponse> clonePostedTemplate(
            @PathVariable Long postedTemplateId
    ) {
        TemplateResponse response = postedTemplateService.clonePostedTemplate(postedTemplateId);

        return BaseResponse.of(
                ResponseCode.SUCCESS,
                response
        );
    }
}
