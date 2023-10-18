package com.together.buytogether.enroll.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.member.domain.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class JoinBuyingTest extends ApiTest {

    @Autowired
    EnrollRepository enrollRepository;
    @Autowired
    SessionManager sessionManager;

    @Test
    @DisplayName("구매 참여")
    void joinBuying() {
        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .joinBuying().cookieValue(sessionManager.getAllSessions().get(0).getId()).request();
        assertThat(enrollRepository.findAll().size()).isEqualTo(1);
    }


}
