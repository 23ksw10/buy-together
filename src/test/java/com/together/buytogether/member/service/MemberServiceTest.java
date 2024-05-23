package com.together.buytogether.member.service;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.dto.request.RegisterMemberDTO;
import com.together.buytogether.member.dto.request.SignInMemberDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.together.buytogether.member.domain.MemberFixture.aMember;
import static com.together.buytogether.member.domain.RegisterMemberDTOFixture.aRegisterMemberDTO;
import static com.together.buytogether.member.domain.SignInMemberDTOFixture.aSignInMemberDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private MemberService memberService;
    private RegisterMemberDTO registerMemberDTO;
    private Member member;

    private SignInMemberDTO signInMemberDTO;

    @BeforeEach
    public void setUp() {
        registerMemberDTO = aRegisterMemberDTO().build();
        member = aMember().build();
        signInMemberDTO = aSignInMemberDTO().build();
    }

    @Test
    @DisplayName("회원가입에 성공한다")
    public void registerMemberSuccess() {
        //given
        given(memberRepository.findByEmail(registerMemberDTO.email())).willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willReturn(registerMemberDTO.toDomain());

        //when
        memberService.registerMember(registerMemberDTO);

        //then
        then(memberRepository).should().findByEmail(registerMemberDTO.email());
        then(memberRepository).should().save(any(Member.class));

        //verify
        verify(memberRepository, times(1)).findByEmail(registerMemberDTO.email());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("이메일 중복은 회원가입에 실패한다")
    public void duplicatedEmailRegisterMemberFail() {
        //given
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        //when
        CustomException ex = assertThrows(CustomException.class, () -> memberService.registerMember(registerMemberDTO));

        //then
        assertEquals(ex.getErrorCode(), ErrorCode.MEMBER_ALREADY_EXIST);
        then(memberRepository).should().findByEmail(member.getEmail());
        then(memberRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    @DisplayName("로그인 - 이메일, 패스워드가 일치하는 사용자는 로그인에 성공한다")
    public void signInSuccess() {
        //given
        given(memberRepository.findByEmail(signInMemberDTO.email())).willReturn(Optional.of(member));

        //when
        memberService.signIn(signInMemberDTO.email(), signInMemberDTO.password());

        //then
        then(memberRepository).should().findByEmail(member.getEmail());

    }

    @Test
    @DisplayName("로그인 - 이메일이 일치하지 않는 사용자는 로그인에 실패한다")
    public void invalidEmailSignInFail() {
        //given
        given(memberRepository.findByEmail(signInMemberDTO.email())).willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            memberService.signIn(signInMemberDTO.email(), signInMemberDTO.password());
        });

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_EMAIL);
    }

    @Test
    @DisplayName("로그인 - 패스워드가 일치하지 않는 사용자는 로그인에 실패한다")
    public void invalidPasswordSignInFail() {
        //given
        SignInMemberDTO wrongPasswordRequest = aSignInMemberDTO().password("wrong-password").build();
        given(memberRepository.findByEmail(wrongPasswordRequest.email())).willReturn(Optional.of(member));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            memberService.signIn(wrongPasswordRequest.email(), wrongPasswordRequest.password());
        });

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
    }

}
