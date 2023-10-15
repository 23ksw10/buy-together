package com.together.buytogether.postcomment.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.SessionManager;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GetCommentTest extends ApiTest {
    @Autowired
    SessionManager sessionManager;

    @Test
    @DisplayName("모든 댓글 조회")
    void getAllComments() {
        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .registerComment().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .registerComment().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .registerComment().cookieValue(sessionManager.getAllSessions().get(0).getId()).request();

        RestAssured.given().log().all()
                .when()
                .get("/posts/1/comments")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("댓글 아이디로 댓글 조회")
    void getComment() {
        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .registerComment().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .registerComment().cookieValue(sessionManager.getAllSessions().get(0).getId()).request();

        RestAssured.given().log().all()
                .when()
                .get("/posts/1/comments/2")
                .then().log().all()
                .statusCode(200);
    }

}
