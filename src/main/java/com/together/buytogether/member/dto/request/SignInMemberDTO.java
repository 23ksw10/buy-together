package com.together.buytogether.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignInMemberDTO(
        @NotBlank(message = "로그인 아이디는 필수 값입니다")
        String email,
        @NotBlank(message = "비밀번호는 필수 값입니다")
        String password) {
}
