package com.together.buytogether.member.feature.api;

import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.feature.SignInMember;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.http.HttpStatus;

public class SignInMemberApi {
    private final String loginId = "loginId";
    private final String password = "password";

    public Scenario request() {
        SignInMember.Request request = new SignInMember.Request(
                loginId,
                password);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/members/sign-in")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        return new Scenario();
    }
}
