package com.together.buytogether.member.service;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.dto.request.RegisterMemberDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Member getLogInMember(String loginId, String password) {
        return memberRepository.findByLoginId(loginId).stream()
                .filter(m -> m.getPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
