package com.linku.backend.global.auth.dto;

import com.linku.backend.domain.user.User;

public record UserInfoResponse(
        String accessToken,
        String refreshToken,
        String googleId,
        String profileImage,
        String name,
        String kuMail
) {
    public static UserInfoResponse from(User user, AuthTokenResponse authTokenResponse) {
        return new UserInfoResponse(
                authTokenResponse.accessToken(),
                authTokenResponse.refreshToken(),
                user.getGoogleId(),     // 구글 이메일이 등록되어 있지 않으면 null이 반환됨
                user.getPicture(),
                user.getName(),
                user.getKuMail()
        );
    }
}
