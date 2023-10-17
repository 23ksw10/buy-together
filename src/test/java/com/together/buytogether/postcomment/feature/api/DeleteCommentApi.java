package com.together.buytogether.postcomment.feature.api;

import com.together.buytogether.common.Scenario;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;

public class DeleteCommentApi {
    private String cookieValue = "";

    public DeleteCommentApi cookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
        return this;
    }

    public Scenario request() {
        RestAssured.given().log().all()
                .when()
                .cookie("JSESSIONID", cookieValue)
                .delete("/posts/1/comments/1")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
        return new Scenario();
    }
}