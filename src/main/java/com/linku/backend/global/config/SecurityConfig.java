package com.linku.backend.global.config;

import com.linku.backend.global.jwt.JwtAuthenticationFilter;
import com.linku.backend.global.jwt.JwtExceptionFilter;
import com.linku.backend.global.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenService jwtTokenService;

    @Value("${spring.front.origin}")
    private String frontOrigin;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .cors(corsCustomizer ->
                        corsCustomizer.configurationSource(corsConfigurationSource()))          //CORS 설정
                .csrf(AbstractHttpConfigurer::disable)                  //CSRF(Cross-Site Request Forgery) 보호 기능 비활성화
                .formLogin(AbstractHttpConfigurer::disable)             //Spring Security의 기본 폼 로그인 방식(Username & Password 로그인 폼) 비활성화
                .httpBasic(AbstractHttpConfigurer::disable);            //HTTP Basic 인증 방식 비활성화

        //경로별 인가 작업
        HttpSecurity roleGuest = httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()        //preflight 요청은 Auth token 없으므로 항상 permit
                        .requestMatchers(
                                "/auth/send-code",
                                "/auth/verify-code"
                        ).hasAuthority("ROLE_GUEST")
                        .requestMatchers(
                                "/api/example/to-be-authenticated",
                                "/api/login/oauth2/code/google",
                                "/api/oauth2/google/login"
                        ).authenticated().anyRequest().permitAll()
                )

                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class);


        //세션을 사용하지 않는 방식(STATELESS)으로 설정
        httpSecurity
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(customizer -> customizer
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                        }));
        return httpSecurity.build();
    }


    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
                ROLE_MEMBER > ROLE_GUEST
                """);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontOrigin));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));

        return request -> configuration;
    }

}
