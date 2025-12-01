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

        // 요청 URI에 따라 기대하는 토큰 타입을 분기
        String expectedType = resolveTokenType(request);

        Authentication authentication = jwtTokenService.validateToken(token, expectedType);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("JWT 인증 성공: {}, type = {}", authentication.getName(), expectedType);
        filterChain.doFilter(request, response);
    }

    /**
     * @return 토큰 값(Bearer 제외)
     * @Brief 토큰 파싱하여 Bearer 타입인지 확인하고 그 부분 잘라내서 반환
     */
    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(org.springframework.http.HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER)) {
            return authorization.substring(BEARER.length());
        }
        return null;
    }

    /**
     * @param request
     * @Brief 요청 URI 에 따라 기대하는 토큰 타입(access / guest)을 결정
     */
    private String resolveTokenType(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // 게스트 토큰으로 접근해야 하는 엔드포인트들
        // TODO: 실제 게스트 전용 엔드포인트 패턴에 맞게 수정하세요.
        if (uri.startsWith("/api/auth/guest")
                || uri.startsWith("/api/auth/send-code")
                || uri.startsWith("/api/auth/verify-code")) {
            return JwtTokenService.GUEST;
        }

        // 기본은 access 토큰으로 처리
        return JwtTokenService.ACCESS;
    }
}
