package com.linku.backend.domain.like.controller;

import com.linku.backend.domain.like.dto.response.LikeResponse;
import com.linku.backend.domain.like.service.LikeService;
import com.linku.backend.global.response.BaseResponse;
import com.linku.backend.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posted-templates")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{postedTemplateId}/like")
    public BaseResponse<LikeResponse> createLike(
            @PathVariable Long postedTemplateId
    ) {
        LikeResponse likeResponse = likeService.createLike(postedTemplateId);
        return BaseResponse.of(
                ResponseCode.LIKE_SUCCESS,
                likeResponse
        );
    }

    @DeleteMapping("/{postedTemplateId}/like")
    public BaseResponse<LikeResponse> deleteLike(
            @PathVariable Long postedTemplateId
    ) {
        LikeResponse likeResponse = likeService.deleteLike(postedTemplateId);
        return BaseResponse.of(
                ResponseCode.UNLIKE_SUCCESS,
                likeResponse
        );
    }
}