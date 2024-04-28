package com.together.buytogether.member.controller;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.member.domain.SessionManager;
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
    private final SessionManager sessionManager;


    public MemberController(
            MemberService memberService,
            SessionManager sessionManager) {
        this.memberService = memberService;
        this.sessionManager = sessionManager;
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
        Member logInMember = memberService.getLogInMember(signInMemberDTO.loginId(), encryptPassword);
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute(SessionConst.LOGIN_MEMBER, logInMember.getMemberId());
        String sessionId = httpSession.getId();
        sessionManager.createSession(sessionId, httpSession);
    }

    @PostMapping("/sign-out")
    public void signOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        sessionManager.invalidateSession(session.getId());
    }
}
