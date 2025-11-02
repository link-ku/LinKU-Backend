package com.linku.backend.global.auth.controller;

import com.linku.backend.global.auth.dto.KUMailRequest;
import com.linku.backend.global.auth.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final MailService mailService;

    @PostMapping("/send-code")
    public ResponseEntity<String> sendAuthCode(@RequestParam KUMailRequest request) {
        String code = mailService.createAuthCode();
        mailService.sendAuthMail(request.kuMail(), code);
        return ResponseEntity.ok("인증코드가 전송되었습니다.");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyAuthCode(@RequestParam String email, @RequestParam String code) {
        boolean isValid = mailService.verifyAuthCode(email, code);
        if (isValid) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.badRequest().body("인증 실패: 코드가 올바르지 않습니다.");
        }
    }
}
