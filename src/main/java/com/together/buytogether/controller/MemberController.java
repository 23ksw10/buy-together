package com.together.buytogether.controller;

import com.together.buytogether.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
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

}
