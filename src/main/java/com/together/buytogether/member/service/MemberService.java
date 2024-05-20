package com.together.buytogether.member.service;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.dto.request.RegisterMemberDTO;
import com.together.buytogether.member.utils.HashingUtil;
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
        validateDuplicateMember(registerMemberDTO.email());
        Member member = registerMemberDTO.toDomain();
        memberRepository.save(member);
    }

    private void validateDuplicateMember(String email) {
        memberRepository.findByEmail(email).stream()
                .findFirst()
                .ifPresent(member -> {
                    throw new CustomException(ErrorCode.MEMBER_ALREADY_EXIST);
                });
    }

    public Member signIn(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_EMAIL));
        String encryptPassword = HashingUtil.encrypt(password);
        if (!encryptPassword.equals(member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        return member;
    }
}
