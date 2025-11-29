package com.linku.backend.domain.oauth.controller;

import com.linku.backend.domain.oauth.service.OAuthService;
import com.linku.backend.global.auth.dto.AuthTokenResponse;
import com.linku.backend.global.response.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.linku.backend.global.response.ResponseCode.SUCCESS;

@RestController
@RequestMapping("/oauth2/google")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping
    public void getGoogleAuthUrl(@RequestParam String redirectUri,HttpServletResponse response) throws IOException {
        String authUrl = oAuthService.createGoogleAuthorizationUrl(redirectUri);
        response.sendRedirect(authUrl);
    }

    @GetMapping("/login")
    public BaseResponse<AuthTokenResponse> loginWithGoogle(@RequestParam String redirectUri, @RequestParam String code) {
        AuthTokenResponse response = oAuthService.loginOrSignupWithGoogle(code, redirectUri);
        return BaseResponse.of(SUCCESS, response);
    }

}