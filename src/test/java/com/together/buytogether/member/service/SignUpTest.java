package com.together.buytogether.member.service;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.member.domain.Gender;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.dto.request.RegisterMemberDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SignUpTest {

    private final String NAME = "name";
    private final String LOGIN_ID = "loginId";
    private final String PASSWORD = "password";
    private final String PHONE_NUMBER = "010-0000-0000";
    private final Gender GENDER = Gender.MALE;
    private final String ADDRESS = "경기도 고양시 덕양구 화정로 27";
    private final String DETAIL_ADDRESS = "625동 1004호";
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private MemberService memberService;
    private RegisterMemberDTO registerMemberDTO;
    private Member member;

    @BeforeEach
    public void setUp() {
        registerMemberDTO = createMember();
        member = registerMemberDTO.toDomain();
    }

    @Test
    @DisplayName("회원가입에 성공한다")
    public void signUp_successful() {
        //given
        given(memberRepository.findByLoginId(registerMemberDTO.loginId())).willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willReturn(registerMemberDTO.toDomain());

        //when
        memberService.registerMember(registerMemberDTO);

        //then
        then(memberRepository).should().findByLoginId(registerMemberDTO.loginId());
        then(memberRepository).should().save(any(Member.class));

        //verify
        verify(memberRepository, times(1)).findByLoginId(registerMemberDTO.loginId());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("아이디 중복은 회원가입에 실패한다")
    public void loginIdDuplicated_registerMember_failure() {
        //given
        given(memberRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.of(member));

        //when
        CustomException ex = assertThrows(CustomException.class, () -> memberService.registerMember(registerMemberDTO));

        //then
        assertEquals(ex.getErrorCode(), ErrorCode.MEMBER_ALREADY_EXIST);
        then(memberRepository).should().findByLoginId(LOGIN_ID);
        then(memberRepository).shouldHaveNoMoreInteractions();

    }

    private RegisterMemberDTO createMember() {
        return RegisterMemberDTO.builder()
                .name(NAME)
                .loginId(LOGIN_ID)
                .password(PASSWORD)
                .phoneNumber(PHONE_NUMBER)
                .gender(GENDER)
                .address(ADDRESS)
                .detailAddress(DETAIL_ADDRESS)
                .build();
    }

}
