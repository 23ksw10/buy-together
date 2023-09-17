package com.together.buytogether.member.feature;

import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SEX;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class RegisterMemberTest {
    private RegisterMember registerMember;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = new MemberRepository();
        registerMember = new RegisterMember(memberRepository);
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
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }

}
