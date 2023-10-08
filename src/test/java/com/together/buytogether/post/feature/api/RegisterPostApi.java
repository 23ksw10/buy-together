package com.together.buytogether.post.feature.api;

import com.together.buytogether.common.Scenario;
import com.together.buytogether.post.feature.RegisterPost;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class RegisterPostApi {
    private String title = "title";
    private String content = "content";
    private LocalDateTime expiredAt = LocalDateTime.now().plusHours(10);

    private String cookieName;

    public RegisterPostApi title(String title) {
        this.title = title;
        return this;
    }

    public RegisterPostApi content(String content) {
        this.content = content;
        return this;
    }

    public RegisterPostApi expiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
        return this;
    }

    public RegisterPostApi cookieName(String cookieName) {
        this.cookieName = cookieName;
        return this;
    }


    public Scenario request() {
        RegisterPost.Request request = new RegisterPost.Request(
                title,
                content,
                expiredAt
        );
        RestAssured.given().log().all()
                .cookie(cookieName)
                .contentType("application/json")
                .body(request)
                .when()
                .post("/posts")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
        return new Scenario();
    }
}
