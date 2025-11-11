package com.linku.backend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "이름은 비어 있을 수 없습니다.")
        @Size(max = 30, message = "이름은 30자 이내로 입력해주세요.")
        String name
) {
}
