package com.linku.backend.global.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final Map<String, String> authCodeStore = new ConcurrentHashMap<>();

    @Value("${spring.mail.username}")
    private String fromEmail;


    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 6자리 인증코드 생성
    public String createAuthCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    // 인증 메일 전송
    public void sendAuthMail(String toEmail, String authCode) {
        String subject = "[LinkU] 이메일 인증 코드입니다.";
        String text = """
                안녕하세요, LinkU 입니다.
                아래 인증코드를 입력해주세요.
                
                ✅ 인증코드: %s
                
                본 메일은 발신전용입니다.
                """.formatted(authCode);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(text);
            helper.setFrom(fromEmail);

            mailSender.send(message);

            // 인증 코드 저장
            authCodeStore.put(toEmail, authCode);

        } catch (MessagingException e) {
            throw new RuntimeException("메일 전송 중 오류가 발생했습니다.", e);
        }
    }

    // 인증 코드 검증
    public boolean verifyAuthCode(String email, String inputCode) {
        String storedCode = authCodeStore.get(email);
        if (storedCode != null && storedCode.equals(inputCode)) {
            authCodeStore.remove(email); // 검증 성공 후 삭제
            return true;
        }
        return false;
    }
}
