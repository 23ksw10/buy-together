package com.together.buytogether.enroll.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.member.domain.SessionManager;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

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
                .registerPost().cookieValue(sessionManager.getAllSessions().get(0).getId()).request();
        Long postId = 1L;
        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionManager.getAllSessions().get(0).getId())
                .when()
                .post("/posts/{postId}/enroll", postId)
                .then().log().all().statusCode(HttpStatus.CREATED.value());
        assertThat(enrollRepository.findAll().size()).isEqualTo(1);
    }

}
