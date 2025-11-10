package com.linku.backend.global.auth.service;

import com.linku.backend.global.exception.LinkuException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static com.linku.backend.global.response.ResponseCode.AUTH_CODE_INVALID;
import static com.linku.backend.global.response.ResponseCode.MAIL_SEND_FAIL;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final Map<String, String> authCodeStore = new ConcurrentHashMap<>();

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendAuthMail(String email) {
        String authCode = createAuthCode();
        log.info("[sendMail] 생성된 인증 코드 = {}", authCode);
        MimeMessage message = mailSender.createMimeMessage();

        String subject = "[LinkU] 이메일 인증 코드입니다.";
        String text = """
                안녕하세요, LinkU 입니다.
                아래 인증코드를 입력해주세요.
                
                ✅ 인증코드: %s
                
                본 메일은 발신전용입니다.
                """.formatted(authCode);

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text);
            helper.setFrom(senderEmail);

            mailSender.send(message);
            authCodeStore.put(email, authCode);

        } catch (MessagingException e) {
            throw LinkuException.of(MAIL_SEND_FAIL);
        }
    }

    private String createAuthCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    public void verifyAuthCode(String email, String inputCode) {
        String stored = authCodeStore.get(email);
        if (stored != null && stored.equals(inputCode)) {
            authCodeStore.remove(email);
            return;
        }
        throw LinkuException.of(AUTH_CODE_INVALID);
    }

}
