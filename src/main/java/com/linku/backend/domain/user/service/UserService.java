package com.linku.backend.domain.user.service;

import com.linku.backend.domain.user.User;
import com.linku.backend.domain.user.dto.SignupRequest;
import com.linku.backend.domain.user.repository.UserRepository;
import com.linku.backend.global.auth.dto.AuthTokenResponse;
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
@Transactional
@Service
public class UserService {
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    public User findOrCreateUser(User guest) {
        Optional<User> optionalUser = userRepository.findByProviderId(guest.getProviderId());
        return optionalUser.orElseGet(() -> userRepository.save(guest));
    }

    public AuthTokenResponse signup(String guestToken, SignupRequest request) {
        Long UserId = jwtTokenService.extractUserIdByGuestToken(guestToken);
        User User = getUserById(UserId);
        log.debug("[signup] 정보를 업데이트 할 User = {}", User);
        User.updateInfo(request);
        return jwtTokenService.generateAuthToken(User);
    }

    public User getUserById(Long UserId) {
        return userRepository.findById(UserId)
                .orElseThrow(() -> LinkuException.of(USER_NOT_FOUND));
    }
}
