package com.linku.backend.domain.user.controller;

import com.linku.backend.domain.user.dto.SignupRequest;
import com.linku.backend.domain.user.service.UserService;
import com.linku.backend.global.auth.dto.AuthTokenResponse;
import com.linku.backend.global.response.BaseResponse;
import com.linku.backend.global.response.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public BaseResponse<AuthTokenResponse> signup(@Valid @RequestBody SignupRequest signupRequest,
                                                  @RequestHeader("Authorization") String guestToken) {
        log.debug("Signup Request: {}", signupRequest);
        AuthTokenResponse response = userService.signup(guestToken, signupRequest);
        return BaseResponse.of(ResponseCode.SUCCESS, response);
    }
}
