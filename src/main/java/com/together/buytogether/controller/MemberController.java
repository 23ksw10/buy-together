package com.together.buytogether.controller;

import com.together.buytogether.domain.Member;
import com.together.buytogether.dto.MemberSignUpDto;
import com.together.buytogether.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    MemberController(MemberService memberService){
        this.memberService = memberService;
    }
    @PostMapping
    public void signUp(@RequestBody MemberSignUpDto signUpDto, BindingResult result){
        if(result.hasErrors()){
            throw new IllegalStateException("올바르지 않은 입력입니다");
        }

        Member member = Member.builder()
                .name(signUpDto.getName())
                .phoneNumber(signUpDto.getPhoneNumber())
                .loginId(signUpDto.getSex())
                .password(signUpDto.getPassword())
                .sex(signUpDto.getSex())
                .build();
        memberService.createMember(member);
    }

    @PostMapping("/siginin")
    public void signIn(){

    }



}
