package com.linku.backend.domain.oauth;

import com.linku.backend.global.exception.LinkuException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.linku.backend.global.auth.AuthRole.ROLE_GUEST;
import static com.linku.backend.global.response.ResponseCode.*;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService defaultOAuth2UserService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            if (!"google".equals(registrationId)) {
                throw LinkuException.of(UNSUPPORTED_PROVIDER);
            }

            OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

            // SecurityContext에 넣길 원하는 권한 지정
            return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority(ROLE_GUEST.toString())),
                    // user attributes
                    Map.of(
                            "provider", Objects.requireNonNull(oAuth2User.getAttribute("sub")),
                            "email", Objects.requireNonNull(oAuth2User.getAttribute("email")),
                            "name", Objects.requireNonNull(oAuth2User.getAttribute("name"))
                    ),
                    "provider"
            );
        } catch (LinkuException e) {
            throw e;
        } catch (OAuth2AuthenticationException e) {
            String errorMessage = e.getError().getErrorCode();
            if ("invalid_grant".equals(errorMessage)) {
                throw LinkuException.of(GOOGLE_INVALID_CODE);
            } else if ("invalid_token".equals(errorMessage)) {
                throw LinkuException.of(GOOGLE_INVALID_ACCESS_TOKEN);
            } else if ("id_token_verification_failed".equals(errorMessage)) {
                throw LinkuException.of(GOOGLE_ID_TOKEN_VERIFICATION_FAILED);
            } else if ("invalid_user_info_response".equals(errorMessage)) {
                throw LinkuException.of(GOOGLE_INVALID_USER_INFO_RESPONSE);
            }
            String message = e.getMessage();
            log.error("error code = {}, message = {}", errorMessage, message);
            throw LinkuException.of(GOOGLE_OAUTH_FAIL);
        }
    }
}
