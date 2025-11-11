package com.linku.backend.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linku.backend.global.exception.LinkuException;
import com.linku.backend.global.response.ResponseCode;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.MissingFormatArgumentException;

public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AccessDeniedException e) {
            setResponse(response, ResponseCode.FORBIDDEN);
        } catch (ExpiredJwtException e) {
            setResponse(response, ResponseCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            setResponse(response, ResponseCode.UNSUPPORTED_TOKEN_TYPE);
        } catch (MissingFormatArgumentException | SignatureException | MalformedJwtException e) {
            setResponse(response, ResponseCode.MALFORMED_TOKEN_TYPE);
        } catch (LinkuException e) {
            setResponse(response, e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            setResponse(response, ResponseCode.INVALID_TOKEN);
        }
    }

    private String getRolePayload(String token) {
        if (token != null) {
            String[] parts = token.split("\\.");
            String jwtWithOutSignature = parts[0] + "." + parts[1] + ".";

            Claims body = Jwts.parser()
                    .parseClaimsJwt(jwtWithOutSignature).getBody();

            return body.get("role", String.class);
        }
        return null;
    }

    private void setResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(code);

        String json = String.format("{\"code\":%d, \"message\":\"%s\",\"success\":%s}", code, message, false);
        response.getWriter().write(json);
    }

    private void setResponse(HttpServletResponse response, ResponseCode responseCode) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(responseCode.getCode());

        String json = String.format("{\"code\":%d, \"message\":\"%s\",\"success\":%s}",
                responseCode.getCode(),
                responseCode.getMessage(),
                responseCode.isSuccess());
        response.getWriter().write(json);
    }
}
