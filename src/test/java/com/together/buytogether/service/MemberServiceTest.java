package com.together.buytogether.service;

import com.together.buytogether.domain.Member;
import com.together.buytogether.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    public void setup(){
        member = fakeMember(1L);
    }
    
    @Test
    @DisplayName("loginId 중복 시 예외 발생")
    public void existingLoginIdFail(){
        given(memberRepository.findByLoginId(member.getLoginId()))
                .willReturn(Optional.of(member));

        assertThrows(IllegalStateException.class, () -> memberService.createMember(member));
    }



    private Member fakeMember(Long memberId){
        Member member = Member.builder()
                .name("김선욱")
                .phoneNumber(("010-1111-1111"))
                .sex("남")
                .loginId("kim")
                .password("kim")
                .build();
        return member;
    }
}
