package com.together.buytogether.common.service;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class CommonMemberService {
    MemberRepository memberRepository;

    public CommonMemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member getMember(Long memberId) {
        return memberRepository.getByMemberId(memberId);
    }
}
