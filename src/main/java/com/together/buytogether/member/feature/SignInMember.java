package com.together.buytogether.member.feature;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.member.domain.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.util.Assert;
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
    public void request(@RequestBody @Valid Request request, HttpServletRequest httpServletRequest) {
        Member logInMember = getLogInMember(request.loginId, request.password);
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

    public record Request(
            @NotBlank(message = "로그인 아이디는 필수 값입니다")
            String loginId,
            @NotBlank(message = "비밀번호는 필수 값입니다")
            String password) {
        public Request {
            Assert.hasText(loginId, "로그인 아이디는 필수 값입니다");
            Assert.hasText(password, "비밀번호는 필수 값입니다");
        }
    }
}
