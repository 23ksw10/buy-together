package com.together.buytogether.member.feature;

import com.together.buytogether.member.domain.Address;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SEX;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SignInMemberTest {
    private final String loginId = "loginId";
    private final String password = "password";
    private final String sessionId = "sessionId";
    private SignInMember signInMember;
    private MemberRepository memberRepository;
    private MockHttpSession httpSession;

    @BeforeEach
    void setUp() {
        memberRepository = Mockito.mock(MemberRepository.class);
        signInMember = new SignInMember();
        httpSession = new MockHttpSession();
    }

    @Test
    @DisplayName("로그인 성공")
    void signUpMember() {
        //given

        Member member = new Member(
                "name",
                "loginId",
                "password",
                "phoneNumber",
                SEX.MALE,
                new Address("address", "detailAddress")
        );
        Mockito.when(memberRepository.findByLoginId(loginId))
                .thenReturn(Optional.of(member));

        //when
        signInMember.request(loginId, password);

        //then
        assertThat(httpSession.getAttribute(sessionId)).isEqualTo(1L);
    }

    private class SignInMember {
        public void request(String loginId, String password) {
            Member logInMember = memberRepository.findByLoginId(loginId).stream()
                    .filter(m -> m.getPassword().equals(password))
                    .findFirst()
                    .orElseThrow(null);
            httpSession.setAttribute("sessionId", 1L);
        }
    }
}
