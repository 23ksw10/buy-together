package com.together.buytogether.member.dto.request;

import com.together.buytogether.member.domain.Address;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.SEX;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.Assert;


public record RegisterMemberDTO(
        @NotBlank(message = "이름은 필수 값입니다")
        String name,
        @NotBlank(message = "로그인 아이디는 필수 값입니다")
        String loginId,
        @NotBlank(message = "비밀번호는 필수 값입니다")
        String password,
        @NotBlank(message = "전화번호는 필수 값입니다")
        String phoneNumber,
        @NotNull(message = "성별은 필수 값입니다")
        SEX sex,
        @NotBlank(message = "주소는 필수 값입니다")
        String address,
        @NotBlank(message = "상세 주소는 필수 값입니다")
        String detailAddress) {
    public RegisterMemberDTO {
        Assert.hasText(name, "이름은 필수 값입니다");
        Assert.hasText(loginId, "로그인 아이디는 필수 값입니다");
        Assert.hasText(password, "비밀번호는 필수 값입니다");
        Assert.hasText(phoneNumber, "전화번호는 필수 값입니다");
        Assert.notNull(sex, "성별은 필수 값입니다");
        Assert.hasText(address, "주소는 필수 값입니다");
        Assert.hasText(detailAddress, "상세 주소는 필수 값입니다");
    }

    public Member toDomain() {
        return new Member(
                name,
                loginId,
                "password",
                "phoneNumber",
                sex,
                new Address(address, detailAddress)
        );
    }
}
