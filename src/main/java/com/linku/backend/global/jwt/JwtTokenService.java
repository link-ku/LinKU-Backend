package com.linku.backend.global.jwt;

import com.linku.backend.domain.user.User;
import com.linku.backend.global.auth.AuthRole;
import com.linku.backend.global.auth.AuthUser;
import com.linku.backend.global.auth.dto.AuthTokenResponse;
import com.linku.backend.global.auth.dto.GuestTokenResponse;
import com.linku.backend.global.exception.LinkuException;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import static com.linku.backend.global.response.ResponseCode.EXPIRED_TOKEN;
import static com.linku.backend.global.response.ResponseCode.INVALID_TOKEN_TYPE;


@Getter
@Slf4j
@Service
public class JwtTokenService {

    private final byte[] secretKey;
    private final long ACCESS_TOKEN_EXPIRATION;
    private final long REFRESH_TOKEN_EXPIRATION;

    public final static String GUEST = "guest";
    public final static String ACCESS = "access";
    public final static String REFRESH = "refresh";

    public JwtTokenService(@Value("${secret.jwt.key}") String secretKey,
                           @Value("${secret.jwt.access.expiration}") long accessTokenExpiration,
                           @Value("${secret.jwt.refresh.expiration}") long refreshTokenExpiration) {

        this.secretKey = Base64.getDecoder().decode(secretKey);
        this.ACCESS_TOKEN_EXPIRATION = accessTokenExpiration;
        this.REFRESH_TOKEN_EXPIRATION = refreshTokenExpiration;
    }

    public AuthTokenResponse generateAuthToken(User user) {
        final Claims claims = Jwts.claims();
        claims.put("sub", user.getUserId());

        AuthRole authRole = user.getAuthRole();
        claims.put("role", authRole);

        String accessToken = generateToken(claims, ACCESS_TOKEN_EXPIRATION, ACCESS);
        String refreshToken = generateToken(claims, REFRESH_TOKEN_EXPIRATION, REFRESH);

        log.info("[generateAuthToken] 토큰을 발급할 회원 id = {}, access token = {}, refresh token = {}", user.getUserId(), accessToken, refreshToken);

        return new AuthTokenResponse(accessToken, refreshToken);
    }

    public GuestTokenResponse generateGuestToken(Long memberId) {
        final Claims claims = Jwts.claims();
        claims.put("sub", memberId);
        claims.put("role", AuthRole.ROLE_GUEST);
        String guestToken = generateToken(claims, ACCESS_TOKEN_EXPIRATION, GUEST);
        log.info("[generateGuestToken] 토큰을 발급할 회원 id = {}, token = {}", memberId, guestToken);
        return new GuestTokenResponse(guestToken);
    }


    private String generateToken(Claims claims, long expiration, String type) {
        claims.put("type", type);
        return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + expiration)).signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    // 토큰 검증 이후에 사용
    private Authentication getAuthentication(Claims claims) {

        Long userId = claims.get("sub", Long.class);
        String role = claims.get("role", String.class);

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(role);
        AuthUser authUser = new AuthUser(userId, authorities);

        return new UsernamePasswordAuthenticationToken(authUser, "", authorities);
    }

    public Authentication validateToken(String rawToken, String type) {
        if (rawToken == null) {
            return null;
        }
        String token = rawToken.replace("Bearer ", "");
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            Claims body = claims.getBody();
            if (body.get("type").equals(type)) {
                return getAuthentication(body);
            }

            throw LinkuException.of(INVALID_TOKEN_TYPE);
        } catch (ExpiredJwtException e) {
            throw LinkuException.of(EXPIRED_TOKEN);
        }
    }

    public Long extractUserIdByGuestToken(String guestToken) {
        AuthUser authMember = (AuthUser) validateToken(guestToken, GUEST).getPrincipal();
        return authMember.getId();
    }
}
