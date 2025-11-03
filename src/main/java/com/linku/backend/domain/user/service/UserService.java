package com.linku.backend.domain.user.service;

import com.linku.backend.domain.user.User;
import com.linku.backend.domain.user.repository.UserRepository;
import com.linku.backend.global.exception.LinkuException;
import com.linku.backend.global.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.linku.backend.global.response.ResponseCode.KUMAIL_ALREADY;
import static com.linku.backend.global.response.ResponseCode.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    @Transactional
    public User findOrCreateUser(User guest) {
        Optional<User> optionalUser = userRepository.findByProviderId(guest.getProviderId());
        return optionalUser.orElseGet(() -> userRepository.save(guest));
    }

    public User getUserById(Long UserId) {
        return userRepository.findById(UserId)
                .orElseThrow(() -> LinkuException.of(USER_NOT_FOUND));
    }

    public void findByKuMail(String kuMail) {
        log.debug("[findByKuMail] 건국대학교 메일 = {}", kuMail);
        userRepository.findByKuMail(kuMail)
                .ifPresent(user -> {
                    throw LinkuException.of(KUMAIL_ALREADY);
                });
    }

    @Transactional
    public void updateInfo(String kuMail, String guestToken) {
        Long userId = jwtTokenService.extractUserIdByGuestToken(guestToken);
        User user = getUserById(userId);
        user.updateInfo(kuMail);
    }
}
