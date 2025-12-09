package com.linku.backend.domain.user.service;

import com.linku.backend.domain.oauth.dto.GoogleUserInfo;
import com.linku.backend.domain.user.User;
import com.linku.backend.domain.user.repository.UserRepository;
import com.linku.backend.global.auth.dto.AuthTokenResponse;
import com.linku.backend.global.auth.dto.UserInfoResponse;
import com.linku.backend.global.exception.LinkuException;
import com.linku.backend.global.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.linku.backend.global.response.ResponseCode.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    @Transactional
    public User findOrCreateUser(GoogleUserInfo userInfo) {
        Optional<User> optionalUser = userRepository.findByProviderId(userInfo.sub());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.updatePictureIfEmpty(userInfo.picture());
            return existingUser;
        }
        return userRepository.save(User.guest(userInfo));
    }

    public User getUserById(Long UserId) {
        return userRepository.findById(UserId)
                .orElseThrow(() -> LinkuException.of(USER_NOT_FOUND));
    }

    @Transactional
    public UserInfoResponse updateInfo(String kuMail, String guestToken) {
        Long userId = jwtTokenService.extractUserIdByGuestToken(guestToken);
        User user = getUserById(userId);
        user.updateInfo(kuMail);
        AuthTokenResponse response = jwtTokenService.generateAuthToken(user);
        return UserInfoResponse.from(user, response);
    }
}
