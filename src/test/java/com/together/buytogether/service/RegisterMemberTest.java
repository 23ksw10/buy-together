package com.together.buytogether.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class RegisterMemberTest {
    private RegisterMember registerMember;

    @BeforeEach
    void setUp() {
        registerMember = new RegisterMember();
    }

    @Test
    @DisplayName("회원가입")
    void registerMember() {
        String address = "경기도 고양시 덕양구 화정로 27";
        String detailAddress = "625동 1004호";
        RegisterMember.Request request = new RegisterMember.Request(
                "name",
                "loginId",
                "password",
                "phoneNumber",
                SEX.MALE,
                address, //도로명 주소
                detailAddress    //상세 주소
        );
        //when
        registerMember.request(request);

        //then
//        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }

    public enum SEX {
        MALE("남자"),
        FEMALE("여자");

        private final String description;

        SEX(String description) {
            this.description = description;
        }
    }

    private class RegisterMember {


        public void request(Request request) {
        }

        public record Request(
                String name,
                String loginId,
                String password,
                String phoneNumber,
                SEX sex,
                String address,
                String detailAddress) {
        }
    }
}
