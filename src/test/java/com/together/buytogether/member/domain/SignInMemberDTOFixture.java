package com.together.buytogether.member.domain;

import com.together.buytogether.member.dto.request.SignInMemberDTO;

public class SignInMemberDTOFixture {
    private String email = "ksw@gmail.com";
    private String password = "password";

    public static SignInMemberDTOFixture aSignInMemberDTO() {
        return new SignInMemberDTOFixture();
    }


    public SignInMemberDTOFixture email(String email) {
        this.email = email;
        return this;
    }

    public SignInMemberDTOFixture password(String password) {
        this.password = password;
        return this;
    }

    public SignInMemberDTO build() {
        return SignInMemberDTO.builder()
                .email(email)
                .password(password)
                .build();
    }

}
