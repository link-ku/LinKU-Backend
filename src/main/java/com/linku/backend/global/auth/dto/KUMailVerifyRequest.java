package com.linku.backend.global.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record KUMailVerifyRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@konkuk\\.ac\\.kr$", message = "건국대학교 이메일(@konkuk.ac.kr)만 가능합니다.")
        String kuMail,

        @NotBlank(message = "인증 코드를 입력해주세요.")
        @Pattern(regexp = "^\\d{6}$", message = "인증 코드는 숫자 6자리여야 합니다.")
        String authCode
) {
}
