package com.together.buytogether.postcomment.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SessionManager;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;


public class UpdateCommentTest extends ApiTest {
    @Autowired
    private UpdateComment updateComment;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private SessionManager sessionManager;


    @Test
    @DisplayName("댓글 수정")
    void updateComment() {
        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .registerComment().cookieValue(sessionManager.getAllSessions().get(0).getId()).request();

        String content = "댓글 수정";
        Long memberId = 1L;
        Long postId = 1L;
        Long commentId = 1L;
        UpdateComment.Request request = new UpdateComment.Request(
                content
        );
        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionManager.getAllSessions().get(0).getId())
                .contentType("application/json")
                .body(request)
                .when()
                .put("/posts/{postId}/comments/{commentId}", postId, commentId)
                .then().log().all()
                .statusCode(200);

        assertThat(postCommentRepository.getByCommentId(commentId).getContent()).isEqualTo(content);
    }

}
