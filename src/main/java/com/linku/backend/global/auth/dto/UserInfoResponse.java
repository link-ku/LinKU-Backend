package com.linku.backend.global.auth.dto;

import com.linku.backend.domain.user.User;

public record UserInfoResponse(
        Long userId,
        String name,
        String kuMail
) {
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getUserId(),
                user.getName(),
                user.getKuMail()
        );
    }
}
