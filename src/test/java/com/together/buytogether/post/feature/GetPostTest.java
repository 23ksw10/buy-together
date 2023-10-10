package com.together.buytogether.post.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.SessionManager;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GetPostTest extends ApiTest {

    @Autowired
    private SessionManager sessionManager;

    @Test
    @DisplayName("모든 게시글 조회")
    void getPosts() {
        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieName(sessionManager.getAllSessions().get(0).getId()).request()
                .registerPost().cookieName(sessionManager.getAllSessions().get(0).getId()).request()
                .registerPost().cookieName(sessionManager.getAllSessions().get(0).getId()).request();

        RestAssured.given().log().all()
                .when()
                .get("/posts")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("게시글 아이디로 게시글 조회")
    void getPostWithPostId() {
        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieName(sessionManager.getAllSessions().get(0).getId()).request()
                .registerPost().cookieName(sessionManager.getAllSessions().get(0).getId()).request()
                .registerPost().cookieName(sessionManager.getAllSessions().get(0).getId()).request();

        RestAssured.given().log().all()
                .when()
                .get("/posts/1")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("잘못된 게시글 아이디로 게시글 조회")
    void getPostWithWrongPostId() {
        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieName(sessionManager.getAllSessions().get(0).getId()).request()
                .registerPost().cookieName(sessionManager.getAllSessions().get(0).getId()).request()
                .registerPost().cookieName(sessionManager.getAllSessions().get(0).getId()).request();

        RestAssured.given().log().all()
                .when()
                .get("/posts/5")
                .then().log().all()
                .statusCode(500);
    }
}
