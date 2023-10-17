package com.together.buytogether.member.feature;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.member.domain.SessionManager;
import com.together.buytogether.member.dto.request.SignInMemberDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignInMember {
    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    public SignInMember(MemberRepository memberRepository, SessionManager sessionManager) {
        this.memberRepository = memberRepository;
        this.sessionManager = sessionManager;
    }


    @PostMapping("/members/sign-in")
    public void request(@RequestBody @Valid SignInMemberDTO signInMemberDTO, HttpServletRequest httpServletRequest) {
        Member logInMember = getLogInMember(signInMemberDTO.loginId(), signInMemberDTO.password());
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute(SessionConst.LOGIN_MEMBER, logInMember.getMemberId());
        String sessionId = httpSession.getId();
        sessionManager.createSession(sessionId, httpSession);
    }

    private Member getLogInMember(String loginId, String password) {
        return memberRepository.findByLoginId(loginId).stream()
                .filter(m -> m.getPassword().equals(password))
                .findFirst()
                .orElseThrow(null);
    }

}
