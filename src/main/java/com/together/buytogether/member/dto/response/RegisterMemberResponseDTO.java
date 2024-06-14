package com.together.buytogether.member.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record RegisterMemberResponseDTO(
        @NotBlank(message = "이름은 필수 값입니다")
        String name,
        @NotBlank(message = "로그인 아이디는 필수 값입니다")
        String email,
        @NotBlank(message = "전화번호는 필수 값입니다")
        String phoneNumber
) {
}
