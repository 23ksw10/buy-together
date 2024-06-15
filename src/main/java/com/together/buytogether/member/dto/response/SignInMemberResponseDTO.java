package com.together.buytogether.member.dto.response;

import lombok.Builder;
import org.springframework.util.Assert;

@Builder
public record SignInMemberResponseDTO(
        String email,
        String name) {
    public SignInMemberResponseDTO {
        Assert.hasText(name, "이름은 필수 값입니다.");
        Assert.hasText(email, "이메일은 필수 값입니다.");
    }
}
