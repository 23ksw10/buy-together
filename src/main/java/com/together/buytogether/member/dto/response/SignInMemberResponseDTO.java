package com.together.buytogether.member.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignInMemberResponseDTO(
        @NotBlank(message = "이메일은 필수 값입니다")
        String email,
        @NotBlank(message = "이름은 필수 값입니다")
        String name) {
}
