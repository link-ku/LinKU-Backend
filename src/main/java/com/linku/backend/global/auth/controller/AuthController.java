package com.linku.backend.global.auth.controller;

import com.linku.backend.domain.user.User;
import com.linku.backend.domain.user.service.UserService;
import com.linku.backend.global.auth.dto.KUMailRequest;
import com.linku.backend.global.auth.dto.KUMailVerifyRequest;
import com.linku.backend.global.auth.service.MailService;
import com.linku.backend.global.jwt.JwtTokenService;
import com.linku.backend.global.response.BaseResponse;
import com.linku.backend.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final MailService mailService;
    private final UserService userService;

    @PostMapping("/send-code")
    public BaseResponse<Void> sendAuthCode(@Validated @RequestBody KUMailRequest request) {
        log.debug("[sendMail] 사용자 메일 = {}", request.kuMail());
        userService.findByKuMail(request.kuMail());
        mailService.sendAuthMail(request.kuMail());

        return BaseResponse.of(ResponseCode.SUCCESS, null);
    }

    @PostMapping("/verify-code")
    public BaseResponse<Void> verifyAuthCode(@Validated @RequestBody KUMailVerifyRequest request,
                                             @RequestHeader("Authorization") String guestToken) {
        log.debug("[verify] 사용자 메일 = {}, 인증코드 = {}", request.kuMail(), request.authCode());
        mailService.verifyAuthCode(request.kuMail(), request.authCode());
        userService.updateInfo(request.kuMail(), guestToken);
        return BaseResponse.of(ResponseCode.SUCCESS, null);
    }
}
