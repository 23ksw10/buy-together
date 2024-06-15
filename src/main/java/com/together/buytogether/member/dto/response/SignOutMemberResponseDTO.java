package com.together.buytogether.member.dto.response;

import org.springframework.util.Assert;

public record SignOutMemberResponseDTO(String message) {
    public SignOutMemberResponseDTO {
        Assert.hasText(message, "메세지는 필수 값입니다.");
    }
}
