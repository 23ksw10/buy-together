package com.together.buytogether.member.domain;

import com.together.buytogether.member.dto.request.SignInMemberDTO;

public class SignInMemberDTOFixture {
    private String loginId = "loginId";
    private String password = "password";

    public static SignInMemberDTOFixture aSignInMemberDTO() {
        return new SignInMemberDTOFixture();
    }


    public SignInMemberDTOFixture loginId(String loginId) {
        this.loginId = loginId;
        return this;
    }

    public SignInMemberDTOFixture password(String password) {
        this.password = password;
        return this;
    }

    public SignInMemberDTO build() {
        return SignInMemberDTO.builder()
                .loginId(loginId)
                .password(password)
                .build();
    }

}
