package com.together.buytogether.member.feature;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.dto.request.RegisterMemberDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterMember {
    private final MemberRepository memberRepository;

    public RegisterMember(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @PostMapping("/members")
    @ResponseStatus(HttpStatus.CREATED)
    public void request(@RequestBody @Valid RegisterMemberDTO registerMemberDTO) {
        // request에서 필요한 값들을 꺼내서 회원 도메인을 생성하고 저장한다
        // 중복 검사도
        validateDuplicateMember(registerMemberDTO.loginId());
        Member member = registerMemberDTO.toDomain();
        memberRepository.save(member);
    }

    private void validateDuplicateMember(String logingId) {
        memberRepository.findAll().stream()
                .filter(member -> member.getLoginId().equals(logingId))
                .findFirst()
                .ifPresent(member -> {
                    throw new IllegalArgumentException("이미 존재하는 회원입니다");
                });
    }

}
