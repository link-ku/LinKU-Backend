package com.linku.backend.domain.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linku.backend.domain.user.User;
import com.linku.backend.domain.user.service.UserService;
import com.linku.backend.global.auth.AuthRole;
import com.linku.backend.global.auth.dto.AuthTokenResponse;
import com.linku.backend.global.auth.dto.GuestTokenResponse;
import com.linku.backend.global.jwt.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        User guest = User.guest((OAuth2User) authentication.getPrincipal());
        User user = userService.findOrCreateUser(guest);

        // 신규회원 -> 회원가입
        if (user.getAuthRole() == AuthRole.ROLE_GUEST) {
            GuestTokenResponse guestTokenResponse = jwtTokenService.generateGuestToken(user.getUserId());
            response.setContentType("application/json;charset=UTF-8");
            String json = objectMapper.writeValueAsString(guestTokenResponse);
            response.getWriter().write(json);
            return;
        }

        // 기존회원 -> 로그인
        AuthTokenResponse authTokenResponse = jwtTokenService.generateAuthToken(user);
        response.setContentType("application/json;charset=UTF-8");
        String json = objectMapper.writeValueAsString(authTokenResponse);
        response.getWriter().write(json);
    }
}
