package com.together.buytogether.member.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SignOutMemberTest extends ApiTest {
    private SignOutMember signOutMember;

    @Autowired
    private SessionManager sessionManager;

    @BeforeEach
    void setUp2() {
        signOutMember = new SignOutMember();
    }


    @Test
    @DisplayName("로그아웃 성공")
    void signOutMember() {
        Scenario.registerMember().request().signInMember().request();
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.when(request.getSession(false)).thenReturn(sessionManager.getAllSessions().get(0));
        signOutMember.requet(request);
        assertThat(sessionManager.getAllSessions().size()).isEqualTo(0);
    }

    private class SignOutMember {
        public void requet(HttpServletRequest request) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            sessionManager.invalidateSession(session.getId());
        }
    }
}
