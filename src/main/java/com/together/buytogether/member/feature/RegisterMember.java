package com.together.buytogether.member.feature;

import com.together.buytogether.member.dto.request.RegisterMemberDTO;
import com.together.buytogether.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterMember {
    private final MemberService memberService;

    public RegisterMember(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members")
    @ResponseStatus(HttpStatus.CREATED)
    public void request(@RequestBody @Valid RegisterMemberDTO registerMemberDTO) {
        memberService.registerMember(registerMemberDTO);
    }

}
