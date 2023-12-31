package com.together.buytogether.post.feature.api;

import com.together.buytogether.common.Scenario;
import com.together.buytogether.post.domain.PostStatus;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class RegisterPostApi {
    private String title = "title";
    private String content = "content";

    private PostStatus status = PostStatus.OPEN;
    private LocalDateTime expiredAt = LocalDateTime.now().plusHours(10);
    private String cookieValue;
    private Long maxJoinCount = 1L;
    private Long joinCount = 0L;

    public RegisterPostApi title(String title) {
        this.title = title;
        return this;
    }

    public RegisterPostApi content(String content) {
        this.content = content;
        return this;
    }

    public RegisterPostApi status(PostStatus status) {
        this.status = status;
        return this;
    }

    public RegisterPostApi expiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
        return this;
    }

    public RegisterPostApi cookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
        return this;
    }

    public RegisterPostApi maxJoinCount(Long maxJoinCount) {
        this.maxJoinCount = maxJoinCount;
        return this;
    }

    public RegisterPostApi joinCount(Long joinCount) {
        this.joinCount = joinCount;
        return this;
    }

    public Scenario request() {
        RegisterPostDTO request = new RegisterPostDTO(
                title,
                content,
                status,
                expiredAt,
                maxJoinCount,
                joinCount
        );
        RestAssured.given().log().all()
                .cookie(cookieValue)
                .contentType("application/json")
                .body(request)
                .when()
                .post("/posts")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
        return new Scenario();
    }
}
