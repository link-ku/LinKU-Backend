package com.linku.backend.global.auth.controller;

import com.linku.backend.global.auth.dto.KUMailRequest;
import com.linku.backend.global.auth.dto.KUMailVerifyRequest;
import com.linku.backend.global.auth.service.MailService;
import com.linku.backend.global.response.BaseResponse;
import com.linku.backend.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.linku.backend.global.response.ResponseCode.AUTH_CODE_INVALID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final MailService mailService;

    @PostMapping("/send-code")
    public BaseResponse<Void> sendAuthCode(@RequestBody KUMailRequest request) {
        log.info("[sendMail] 사용자 메일 = {}", request.kuMail());
        mailService.sendAuthMail(request.kuMail());

        return BaseResponse.of(
                ResponseCode.SUCCESS, null
        );
    }

    @PostMapping("/verify-code")
    public BaseResponse<Void> verifyAuthCode(@RequestBody KUMailVerifyRequest request) {
        boolean isValid = mailService.verifyAuthCode(request.kuMail(), request.authCode());
        if (isValid) {
            return BaseResponse.of(
                    ResponseCode.SUCCESS, null
            );
        } else {
            return BaseResponse.of(
                    AUTH_CODE_INVALID, null
            );
        }
    }
}
