package com.together.buytogether.member.feature;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SessionConst;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;

import java.util.Optional;

import static com.together.buytogether.member.domain.MemberFixture.aMember;
import static org.assertj.core.api.Assertions.assertThat;

public class SignInMemberTest {

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
        final String loginId = "loginId";
        final String password = "password";
        Member member = aMember().build();
        Mockito.when(memberRepository.findByLoginId(loginId))
                .thenReturn(Optional.of(member));

        //when
        signInMember.request(loginId, password);

        //then
        assertThat(httpSession.getAttribute(SessionConst.LOGIN_MEMBER)).isEqualTo(1L);
    }

    private class SignInMember {
        public void request(String loginId, String password) {
            Member logInMember = getLogInMember(loginId, password);
            httpSession.setAttribute(SessionConst.LOGIN_MEMBER, logInMember.getMemberId());
        }

        private Member getLogInMember(String loginId, String password) {
            return memberRepository.findByLoginId(loginId).stream()
                    .filter(m -> m.getPassword().equals(password))
                    .findFirst()
                    .orElseThrow(null);
        }
    }

}
