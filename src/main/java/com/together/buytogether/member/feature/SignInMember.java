package com.together.buytogether.member.feature;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.member.domain.SessionManager;
import com.together.buytogether.member.dto.request.SignInMemberDTO;
import com.together.buytogether.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignInMember {
    private final MemberService memberService;
    private final SessionManager sessionManager;

    public SignInMember(MemberService memberService, SessionManager sessionManager) {
        this.memberService = memberService;
        this.sessionManager = sessionManager;
    }


    @PostMapping("/members/sign-in")
    public void request(@RequestBody @Valid SignInMemberDTO signInMemberDTO, HttpServletRequest httpServletRequest) {
        Member logInMember = memberService.getLogInMember(signInMemberDTO.loginId(), signInMemberDTO.password());
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute(SessionConst.LOGIN_MEMBER, logInMember.getMemberId());
        String sessionId = httpSession.getId();
        sessionManager.createSession(sessionId, httpSession);
    }

}
