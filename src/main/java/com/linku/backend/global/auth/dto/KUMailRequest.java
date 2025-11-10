package com.linku.backend.global.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record KUMailRequest(
        @NotBlank(message = "건국대학교 이메일을 입력해주세요.")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@konkuk\\.ac\\.kr$", message = "건국대학교 이메일(@konkuk.ac.kr)만 가능합니다.")
        String kuMail
) {
}
