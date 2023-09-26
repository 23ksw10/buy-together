package com.together.buytogether.member.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class SignOutMemberTest extends ApiTest {

    @Autowired
    private SessionManager sessionManager;


    @Test
    @DisplayName("로그아웃 성공")
    void signOutMember() {
        Scenario.registerMember().request()
                .signInMember().request()
                .signOutMember().cookieValue(sessionManager.getAllSessions().get(0).getId()).request();


        assertThat(sessionManager.getAllSessions().size()).isEqualTo(0);
    }

}
