package com.together.buytogether.member.controller;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.member.dto.request.RegisterMemberDTO;
import com.together.buytogether.member.dto.request.SignInMemberDTO;
import com.together.buytogether.member.service.MemberService;
import com.together.buytogether.member.utils.HashingUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;


    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody @Valid RegisterMemberDTO registerMemberDTO) {
        memberService.registerMember(registerMemberDTO);
    }

    @PostMapping("/sign-in")
    public void signIn(
            @RequestBody @Valid SignInMemberDTO signInMemberDTO,
            HttpServletRequest httpServletRequest) {
        String encryptPassword = HashingUtil.encrypt(signInMemberDTO.password());
        Member logInMember = memberService.signIn(signInMemberDTO.loginId(), encryptPassword)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ID_PW));
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute(SessionConst.LOGIN_MEMBER, logInMember.getLoginId());

    }

    @PostMapping("/sign-out")
    public void signOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
