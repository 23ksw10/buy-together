package com.together.buytogether.member.dto.request;

import com.together.buytogether.member.domain.Address;
import com.together.buytogether.member.domain.Gender;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.utils.HashingUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
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
        Gender gender,
        @NotBlank(message = "주소는 필수 값입니다")
        String address,
        @NotBlank(message = "상세 주소는 필수 값입니다")
        String detailAddress) {

    public Member toDomain() {
        return new Member(
                name,
                loginId,
                HashingUtil.encrypt(password),
                phoneNumber,
                gender,
                new Address(address, detailAddress)
        );
    }
}
