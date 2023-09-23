package com.together.buytogether.member.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class SignInMemberTest extends ApiTest {

    @Autowired
    private MemberRepository memberRepository;


    @Test
    @DisplayName("로그인 성공")
    void signUpMember() {
        Scenario.registerMember().request().signInMember().request();

        Member member = memberRepository.findByLoginId("loginId").stream()
                .filter(m -> m.getPassword().equals("password"))
                .findFirst()
                .orElseThrow(null);
        assertThat(member.getPassword()).isEqualTo("password");

    }

}
