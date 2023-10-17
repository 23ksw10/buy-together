package com.together.buytogether.postcomment.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.SessionManager;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteCommentTest extends ApiTest {
    @Autowired
    private DeleteComment deleteComment;
    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private SessionManager sessionManager;

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment() {
        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .registerComment().cookieValue(sessionManager.getAllSessions().get(0).getId()).request();

        RestAssured.given().log().all()
                .when()
                .cookie("JSESSIONID", sessionManager.getAllSessions().get(0).getId())
                .delete("/posts/1/comments/1")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        assertThat(postCommentRepository.findAll()).hasSize(0);
    }

}
