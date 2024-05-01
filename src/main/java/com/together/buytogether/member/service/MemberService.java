package com.together.buytogether.member.service;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.dto.request.RegisterMemberDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void registerMember(RegisterMemberDTO registerMemberDTO) {
        validateDuplicateMember(registerMemberDTO.loginId());
        Member member = registerMemberDTO.toDomain();
        memberRepository.save(member);
    }

    private void validateDuplicateMember(String loginId) {
        memberRepository.findByLoginId(loginId).stream()
                .findFirst()
                .ifPresent(member -> {
                    throw new CustomException(ErrorCode.MEMBER_ALREADY_EXIST);
                });
    }

    public Optional<Member> signIn(String loginId, String password) {
        return memberRepository.findByLoginIdAndPassword(loginId, password);
    }
}
