package com.together.buytogether.member.feature;

import com.together.buytogether.member.domain.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SignOutMember {
    private final SessionManager sessionManager;

    SignOutMember(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @PostMapping("/members/sign-out")
    public void requet(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        sessionManager.invalidateSession(session.getId());
    }
}
