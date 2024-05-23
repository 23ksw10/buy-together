package com.together.buytogether.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.util.Assert;

@Builder
public record SignInMemberDTO(
        @NotBlank(message = "로그인 아이디는 필수 값입니다")
        String email,
        @NotBlank(message = "비밀번호는 필수 값입니다")
        String password) {
    public SignInMemberDTO {
        Assert.hasText(email, "로그인 아이디는 필수 값입니다");
        Assert.hasText(password, "비밀번호는 필수 값입니다");
    }
}
