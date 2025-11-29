package com.linku.backend.domain.oauth.service;

import com.linku.backend.domain.oauth.dto.GoogleTokenResponse;
import com.linku.backend.domain.oauth.dto.GoogleUserInfo;
import com.linku.backend.domain.user.User;
import com.linku.backend.domain.user.service.UserService;
import com.linku.backend.global.auth.AuthRole;
import com.linku.backend.global.auth.dto.AuthTokenResponse;
import com.linku.backend.global.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final RestClient restClient;
    private final UserService userService;
    private final JwtTokenService jwtTokenService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    public String createGoogleAuthorizationUrl(String redirectUri) {
        String state = UUID.randomUUID().toString();

        String scope = String.join(" ",
                "openid",
                "email",
                "profile",
                "https://www.googleapis.com/auth/userinfo.email",
                "https://www.googleapis.com/auth/userinfo.profile"
        );

        return UriComponentsBuilder
                .fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scope)
                .queryParam("state", state)
                .build()
                .encode()
                .toUriString();
    }

    public AuthTokenResponse loginOrSignupWithGoogle(String code, String redirectUri) {
        // 1. 인가 코드로 구글 액세스 토큰 교환
        GoogleTokenResponse tokenResponse = exchangeCodeForToken(code, redirectUri);

        // 2. 액세스 토큰으로 구글 사용자 정보 조회
        GoogleUserInfo userInfo = fetchUserInfo(tokenResponse.accessToken());

        // 3. 이미 존재하면 기존 유저, 없으면 신규 유저 생성
        User user = userService.findOrCreateUser(userInfo);

        // 4. 권한에 따라 다른 토큰 발급 (게스트 / 일반 사용자)
        if (user.getAuthRole() == AuthRole.ROLE_GUEST) {
            return new AuthTokenResponse(jwtTokenService.generateGuestToken(user.getUserId()), null);
        }

        return jwtTokenService.generateAuthToken(user);
    }


    private GoogleTokenResponse exchangeCodeForToken(String code, String redirectUri) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        return restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(GoogleTokenResponse.class);
    }

    private GoogleUserInfo fetchUserInfo(String accessToken) {
        return restClient.get()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(GoogleUserInfo.class);
    }
}