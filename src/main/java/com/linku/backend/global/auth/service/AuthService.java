package com.linku.backend.global.auth.service;

import com.linku.backend.domain.user.User;
import com.linku.backend.domain.user.service.UserService;
import com.linku.backend.global.auth.AuthUser;
import com.linku.backend.global.auth.dto.AuthTokenResponse;
import com.linku.backend.global.auth.dto.UserInfoResponse;
import com.linku.backend.global.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    @Transactional
    public UserInfoResponse reissueToken(String refreshToken) {
        AuthUser authUser = (AuthUser) jwtTokenService.validateToken(refreshToken, JwtTokenService.REFRESH);
        User user = userService.getUserById(authUser.getId());
        AuthTokenResponse authToken = jwtTokenService.generateAuthToken(user);
        return UserInfoResponse.from(user, authToken);
    }
}