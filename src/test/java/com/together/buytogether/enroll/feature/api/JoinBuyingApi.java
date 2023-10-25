package com.together.buytogether.enroll.feature.api;

import com.together.buytogether.common.Scenario;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;

public class JoinBuyingApi {

    private Long postId = 1L;
    private String cookieValue = "";

    public JoinBuyingApi postId(Long postId) {
        this.postId = postId;
        return this;
    }

    public JoinBuyingApi cookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
        return this;
    }

    public Scenario request() {
        RestAssured.given().log().all()
                .cookie("JSESSIONID", cookieValue)
                .when()
                .post("/posts/{postId}/enrolls", postId)
                .then().log().all().statusCode(HttpStatus.CREATED.value());
        return new Scenario();
    }
}