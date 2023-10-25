package com.together.buytogether.member.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterMemberTest extends ApiTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입")
    void registerMember() {
        Scenario.registerMember().request();
        //then
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }

}
