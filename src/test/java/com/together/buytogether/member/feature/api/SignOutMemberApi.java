package com.together.buytogether.member.feature.api;

import com.together.buytogether.common.Scenario;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;

public class SignOutMemberApi {
    String cookieValue;

    public SignOutMemberApi cookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
        return this;
    }


    public Scenario request() {
        RestAssured.given().log().all()
                .cookie("JSESSIONID", cookieValue)
                .when().post("/members/sign-out")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
        return new Scenario();
    }
}
