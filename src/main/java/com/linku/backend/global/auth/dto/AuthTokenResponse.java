package com.linku.backend.global.auth.dto;

public record AuthTokenResponse(String accessToken, String refreshToken) {
}

