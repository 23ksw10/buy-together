package com.together.buytogether.post.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.SessionManager;
import com.together.buytogether.post.domain.PostRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

public class DeletePostTest extends ApiTest {
    @Autowired
    DeletePost deletePost;
    @Autowired
    PostRepository postRepository;
    @Autowired
    SessionManager sessionManager;

    @Test
    @DisplayName("게시글 삭제")
    @Transactional
    void deletePost() {
        Long postId = 1L;
        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieName(sessionManager.getAllSessions().get(0).getId()).request();

        RestAssured.given().log().all()
                .when()
                .cookie("JSESSIONID", sessionManager.getAllSessions().get(0).getId())
                .delete("/posts/{postId}", postId)
                .then().log().all()
                .statusCode(200);
        assertThat(postRepository.findAll()).hasSize(0);

    }

}
