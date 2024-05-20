package com.together.buytogether.member.domain;

import com.together.buytogether.member.utils.HashingUtil;

public class MemberFixture {

    private Long memberId = 1L;

    private String name = "name";

    private String email = "ksw@gmail.com";

    private String password = "password";

    private String phoneNumber = "010-0000-0000";

    private Gender gender = Gender.MALE;
    private AddressFixture addressFixture = AddressFixture.aAddress();


    public static MemberFixture aMember() {
        return new MemberFixture();
    }

    public static MemberFixture signUpMemberDTO() {
        return new MemberFixture();
    }

    public MemberFixture memberId(Long memberId) {
        this.memberId = memberId;
        return this;
    }

    public MemberFixture name(String name) {
        this.name = name;
        return this;
    }

    public MemberFixture email(String email) {
        this.email = email;
        return this;
    }

    public MemberFixture password(String password) {
        this.password = password;
        return this;
    }

    public MemberFixture phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public MemberFixture gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public MemberFixture address(AddressFixture address) {
        this.addressFixture = address;
        return this;
    }

    public Member build() {
        return Member.builder()
                .name(name)
                .email(email)
                .password(HashingUtil.encrypt(password))
                .phoneNumber(phoneNumber)
                .gender(gender)
                .address(addressFixture.build())
                .build();
    }

}