package com.linku.backend.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    private static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token == null) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }
        Authentication authentication = jwtTokenService.validateToken(token, JwtTokenService.ACCESS);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("JWT 인증 성공: {}", authentication.getName());
        tokenValidateAndAuthorization(token);
        filterChain.doFilter(request, response);
    }

    /**
     * @return 토큰 값(Bearer 제외)
     * @Brief 토큰 파싱하여 Bearer 타입인지 확인하고 그 부분 잘라내서 반환
     */
    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER)) {
            return authorization.substring(BEARER.length());
        }
        return null;
    }

    /**
     * @param token
     * @Brief 토큰 검증하고 인가 처리
     */
    private void tokenValidateAndAuthorization(String token) {
        SecurityContextHolder.getContext().setAuthentication(
                jwtTokenService.validateToken(token, JwtTokenService.ACCESS));
    }
}
