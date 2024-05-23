package com.together.buytogether.post.domain;

import com.together.buytogether.post.dto.request.RegisterPostDTO;

import java.time.LocalDateTime;

public class RegisterPostDTOFixture {
    private String title = "title";
    private String content = "content";

    private Long joinCount = 0L;
    private Long maxJoinCount = 100L;

    private PostStatus satus = PostStatus.OPEN;
    private LocalDateTime expiredAt = LocalDateTime.now();

    public static RegisterPostDTOFixture aRegisterPostDTO() {
        return new RegisterPostDTOFixture();
    }

    public RegisterPostDTOFixture title(String title) {
        this.title = title;
        return this;
    }

    public RegisterPostDTOFixture content(String content) {
        this.content = content;
        return this;
    }

    public RegisterPostDTOFixture maxJoinCount(Long maxJoinCount) {
        this.maxJoinCount = maxJoinCount;
        return this;
    }

    public RegisterPostDTOFixture expiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
        return this;
    }

    public RegisterPostDTOFixture joinCount(Long joinCount) {
        this.joinCount = joinCount;
        return this;
    }

    public RegisterPostDTOFixture status(PostStatus status) {
        this.satus = status;
        return this;
    }

    public RegisterPostDTO build() {
        return RegisterPostDTO.builder()
                .title(title)
                .content(content)
                .status(satus)
                .joinCount(joinCount)
                .maxJoinCount(maxJoinCount)
                .expiredAt(expiredAt)
                .build();
    }
}
