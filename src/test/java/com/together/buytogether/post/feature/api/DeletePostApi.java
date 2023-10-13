package com.together.buytogether.post.feature.api;

import com.together.buytogether.common.Scenario;
import io.restassured.RestAssured;

public class DeletePostApi {
    private Long postId = 1L;
    private String sessionId = "sessionId";

    public DeletePostApi postId(Long postId) {
        this.postId = postId;
        return this;
    }

    public DeletePostApi sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public Scenario request() {
        RestAssured.given().log().all()
                .when()
                .cookie("JSESSIONID", sessionId)
                .delete("/posts/{postId}", postId)
                .then().log().all()
                .statusCode(200);
        return new Scenario();
    }
}