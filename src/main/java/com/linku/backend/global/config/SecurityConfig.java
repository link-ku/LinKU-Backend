package com.linku.backend.global.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static com.linku.backend.global.auth.AuthRole.GUEST;
import static com.linku.backend.global.auth.AuthRole.MEMBER;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.front.origin}")
    private String frontOrigin;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DefaultOAuth2UserService defaultOAuth2UserService() {
        return new DefaultOAuth2UserService();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        //CORS 설정
        httpSecurity
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(List.of(frontOrigin));
                        configuration.setAllowedHeaders(List.of("*"));
                        configuration.setAllowedMethods(List.of("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));

                        return configuration;
                    }
                }));

        //CSRF(Cross-Site Request Forgery) 보호 기능 비활성화
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        //Spring Security의 기본 폼 로그인 방식(Username & Password 로그인 폼) 비활성화
        httpSecurity.formLogin(AbstractHttpConfigurer::disable);

        //HTTP Basic 인증 방식 비활성화
        httpSecurity.httpBasic(AbstractHttpConfigurer::disable);

        //경로별 인가 작업
        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/example/to-be-authenticated",
                                "/api/oauth2/authorization/google",
                                "/error", "/actuator/health", "/index.html",
                                "/favicon.ico", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/signup").hasRole(GUEST.getValue())
//                        .requestMatchers(HttpMethod.POST).hasRole(MEMBER.getValue())
                        .anyRequest().authenticated()
                )

                .exceptionHandling(customizer -> customizer
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                        }));

        //세션을 사용하지 않는 방식(STATELESS)으로 설정
        httpSecurity
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return httpSecurity.build();
    }


    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
                ROLE_ADMIN > ROLE_MEMBER > ROLE_GUEST
                """);
    }
}
