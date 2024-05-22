package com.together.buytogether.member.domain;

import com.together.buytogether.member.dto.request.RegisterMemberDTO;

public class RegisterMemberDTOFixture {
    private String name = "name";
    private String email = "ksw@gmail.com";
    private String password = "password";
    private String phoneNumber = "010-0000-0000";
    private Gender gender = Gender.MALE;
    private String address = "경기도 고양시 00구 00로";
    private String detailAddress = "600동 0000호";

    public static RegisterMemberDTOFixture aRegisterMemberDTO() {
        return new RegisterMemberDTOFixture();
    }

    public RegisterMemberDTOFixture name(String name) {
        this.name = name;
        return this;
    }

    public RegisterMemberDTOFixture email(String email) {
        this.email = email;
        return this;
    }

    public RegisterMemberDTOFixture password(String password) {
        this.password = password;
        return this;
    }

    public RegisterMemberDTOFixture phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public RegisterMemberDTOFixture gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public RegisterMemberDTOFixture address(String address) {
        this.address = address;
        return this;
    }

    public RegisterMemberDTOFixture detailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
        return this;
    }

    public RegisterMemberDTO build() {
        return RegisterMemberDTO.builder()
                .name(name)
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .address(address)
                .detailAddress(detailAddress)
                .build();
    }

}
