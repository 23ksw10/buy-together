package com.together.buytogether.member.dto.response;

import lombok.Builder;
import org.springframework.util.Assert;


@Builder
public record RegisterMemberResponseDTO(
        String name,
        String email,
        String phoneNumber
) {
    public RegisterMemberResponseDTO {
        Assert.hasText(name, "이름은 필수 값입니다.");
        Assert.hasText(email, "이메일은 필수 값입니다.");
        Assert.hasText(phoneNumber, "전화번호는 필수 값입니다.");
    }
}
