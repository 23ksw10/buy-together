package com.together.buytogether.comment.feature;

import com.together.buytogether.comment.domain.CommentRepository;
import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.SessionManager;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterCommentTest extends ApiTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    RegisterComment registerComment;
    @Autowired
    SessionManager sessionManager;


    @Test
    @DisplayName("댓글 등록")
    void registerComment() {
        RegisterComment.Request request = new RegisterComment.Request(
                "댓글 내용",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieName(sessionManager.getAllSessions().get(0).getId()).request();
        Long memberId = 1L;
        Long postId = 1L;
        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionManager.getAllSessions().get(0).getId())
                .contentType("application/json")
                .body(request)
                .when()
                .post("/posts/{postId}/comments", postId)
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
        assertThat(commentRepository.findAll().size()).isEqualTo(1);
        assertThat(commentRepository.getByCommentId(1L).getContent()).isEqualTo("댓글 내용");

    }

}
