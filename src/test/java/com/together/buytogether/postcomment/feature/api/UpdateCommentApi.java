package com.together.buytogether.postcomment.feature.api;

import com.together.buytogether.common.Scenario;
import com.together.buytogether.postcomment.feature.UpdateComment;
import io.restassured.RestAssured;

public class UpdateCommentApi {
    private String content = "댓글 수정";
    private Long postId = 1L;
    private Long commentId = 1L;
    private String cookieValue = "";

    public UpdateCommentApi content(String content) {
        this.content = content;
        return this;
    }

    public UpdateCommentApi postId(Long postId) {
        this.postId = postId;
        return this;
    }

    public UpdateCommentApi commentId(Long commentId) {
        this.commentId = commentId;
        return this;
    }

    public UpdateCommentApi cookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
        return this;
    }

    public Scenario request() {
        UpdateComment.Request request = new UpdateComment.Request(
                content
        );
        RestAssured.given().log().all()
                .cookie("JSESSIONID", cookieValue)
                .contentType("application/json")
                .body(request)
                .when()
                .put("/posts/{postId}/comments/{commentId}", postId, commentId)
                .then().log().all()
                .statusCode(200);
        return new Scenario();
    }
}