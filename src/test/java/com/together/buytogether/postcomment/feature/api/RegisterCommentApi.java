package com.together.buytogether.postcomment.feature.api;

import com.together.buytogether.common.Scenario;
import com.together.buytogether.postcomment.feature.RegisterComment;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class RegisterCommentApi {
    private Long postId = 1L;
    private String content = "댓글 내용";
    private LocalDateTime createAt = LocalDateTime.now();
    private LocalDateTime updateAt = LocalDateTime.now();

    private String cookieValue = "";

    public RegisterCommentApi postId(Long postId) {
        this.postId = postId;
        return this;
    }

    public RegisterCommentApi content(String content) {
        this.content = content;
        return this;
    }

    public RegisterCommentApi createAt(LocalDateTime createAt) {
        this.createAt = createAt;
        return this;
    }

    public RegisterCommentApi updateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
        return this;
    }

    public RegisterCommentApi cookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
        return this;
    }


    public Scenario request() {
        RegisterComment.Request request = new RegisterComment.Request(
                content,
                createAt,
                updateAt
        );
        RestAssured.given().log().all()
                .cookie("JSESSIONID", cookieValue)
                .contentType("application/json")
                .body(request)
                .when()
                .post("/posts/{postId}/comments", postId)
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
        return new Scenario();
    }
}