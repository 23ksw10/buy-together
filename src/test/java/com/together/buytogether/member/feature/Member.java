package com.together.buytogether.member.feature;

import org.springframework.util.Assert;

public class Member {
    private final String name;
    private final String loginId;
    private final String password;
    private final String phoneNumber;
    private final SEX sex;
    private final Address address;
    private Long memberId;

    public Member(
            String name,
            String loginId,
            String password,
            String phoneNumber,
            SEX sex,
            Address address) {
        validateMember(name, loginId, password, phoneNumber, sex, address);
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.sex = sex;
        this.address = address;
    }

    private void validateMember(
            String name,
            String loginId,
            String password,
            String phoneNumber,
            SEX sex,
            Address address) {
        Assert.hasText(name, "이름은 필수 값입니다");
        Assert.hasText(loginId, "로그인 아이디는 필수 값입니다");
        Assert.hasText(password, "비밀번호는 필수 값입니다");
        Assert.hasText(phoneNumber, "전화번호는 필수 값입니다");
        Assert.notNull(sex, "성별은 필수 값입니다");
        Assert.notNull(address, "주소는 필수 값입니다");
    }

    public void assignId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getId() {
        return memberId;
    }
}
