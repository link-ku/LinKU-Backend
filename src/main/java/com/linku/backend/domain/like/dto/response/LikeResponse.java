package com.linku.backend.domain.like.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeResponse {
    private final boolean isLiked;
    private final int likeCount;

    public static LikeResponse of(boolean isLiked, int likeCount) {
        return LikeResponse.builder()
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }
}