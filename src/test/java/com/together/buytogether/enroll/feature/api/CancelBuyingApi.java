package com.together.buytogether.enroll.feature.api;

import com.together.buytogether.common.Scenario;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;

public class CancelBuyingApi {
    private Long postId = 1L;
    private String cookieValue = "";

    public CancelBuyingApi postId(Long postId) {
        this.postId = postId;
        return this;
    }

    public CancelBuyingApi cookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
        return this;
    }

    public Scenario request() {
        RestAssured.given().log().all()
                .cookie("JSESSIONID", cookieValue)
                .when()
                .delete("/posts/1/enrolls")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
        return new Scenario();
    }
}