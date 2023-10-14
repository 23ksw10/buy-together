package com.together.buytogether.post.feature.api;

import com.together.buytogether.common.Scenario;
import io.restassured.RestAssured;

public class DeletePostApi {
    private Long postId = 1L;
    private String cookieValue = "sessionId";

    public DeletePostApi postId(Long postId) {
        this.postId = postId;
        return this;
    }

    public DeletePostApi cookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
        return this;
    }

    public Scenario request() {
        RestAssured.given().log().all()
                .when()
                .cookie("JSESSIONID", cookieValue)
                .delete("/posts/{postId}", postId)
                .then().log().all()
                .statusCode(200);
        return new Scenario();
    }
}