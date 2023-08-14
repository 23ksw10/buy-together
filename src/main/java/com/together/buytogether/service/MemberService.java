package com.together.buytogether.service;

import com.together.buytogether.domain.Member;
import com.together.buytogether.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Long createMember(Member member){
        checkDuplicateMember(member);
        return memberRepository.save(member).getId();
    }

    private void checkDuplicateMember(Member member){
        Optional<Member> memberFounded = memberRepository.findByLoginId(member.getLoginId());
        if(memberFounded.isPresent()){
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    public Optional<Member> findById(Long memberId){
        return memberRepository.findById(memberId);
    }
}
