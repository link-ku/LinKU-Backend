package com.linku.backend.global.auth.dto;

import com.linku.backend.domain.user.User;

public record UserInfoResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String name,
        String kuMail
) {
    public static UserInfoResponse from(User user, AuthTokenResponse authTokenResponse) {
        return new UserInfoResponse(
                authTokenResponse.accessToken(),
                authTokenResponse.refreshToken(),
                user.getUserId(),
                user.getName(),
                user.getKuMail()
        );
    }
}
