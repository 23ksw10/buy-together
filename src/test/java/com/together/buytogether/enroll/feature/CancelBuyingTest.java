package com.together.buytogether.enroll.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.member.domain.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class CancelBuyingTest extends ApiTest {
    @Autowired
    private EnrollRepository enrollRepository;
    @Autowired
    private SessionManager sessionManager;


    @Test
    @DisplayName("구매 취소")
    void cancelBuying() {
        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .joinBuying().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .cancelBuying().cookieValue(sessionManager.getAllSessions().get(0).getId()).request();
        assertThat(enrollRepository.findAll().size()).isEqualTo(0);
    }

}
