package com.together.buytogether.member.dto.response;

import jakarta.validation.constraints.NotBlank;

public record SignOutMemberResponseDTO(@NotBlank(message = "메세지 값은 필수입니다") String message) {
}
