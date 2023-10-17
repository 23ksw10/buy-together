package com.together.buytogether.post.feature.api;

import com.together.buytogether.common.Scenario;
import com.together.buytogether.post.domain.PostStatus;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class UpdatePostApi {
    private Long postId = 1L;
    private String newTitle = "newTitle";
    private String newContent = "newContent";

    private PostStatus status = PostStatus.OPEN;

    private LocalDateTime expiredAt = LocalDateTime.now().plusDays(2);
    private String cookieValue = "";

    public UpdatePostApi postId(Long postId) {
        this.postId = postId;
        return this;
    }

    public UpdatePostApi newTitle(String newTitle) {
        this.newTitle = newTitle;
        return this;
    }

    public UpdatePostApi newContent(String newContent) {
        this.newContent = newContent;
        return this;
    }

    public UpdatePostApi status(PostStatus status) {
        this.status = status;
        return this;
    }

    public UpdatePostApi expiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
        return this;
    }

    public UpdatePostApi cookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
        return this;
    }

    public Scenario request() {
        UpdatePostDTO request = new UpdatePostDTO(
                newTitle,
                newContent,
                PostStatus.OPEN,
                expiredAt
        );

        RestAssured.given().log().all()
                .contentType("application/json")
                .body(request)
                .cookie("JSESSIONID", cookieValue)
                .when().put("/posts/{postId}/update", postId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
        return new Scenario();
    }
}