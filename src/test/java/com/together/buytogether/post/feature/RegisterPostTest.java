package com.together.buytogether.post.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.SessionManager;
import com.together.buytogether.post.domain.PostRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterPostTest extends ApiTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private SessionManager sessionManager;


    @Test
    @DisplayName("게시글 등록")
    void registerPost() {
        Scenario.registerMember().request().signInMember().request();
        Long memberId = 1L;
        String title = "title";
        String content = "content";
        LocalDateTime expiredAt = LocalDateTime.now().plusHours(10);
        RegisterPost.Request request = new RegisterPost.Request(
                title,
                content,
                expiredAt
        );
        RestAssured.given().log().all()
                .cookie(sessionManager.getAllSessions().get(0).getId())
                .contentType("application/json")
                .body(request)
                .when()
                .post("/posts")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
        assertThat(postRepository.findAll()).hasSize(1);

    }

}
