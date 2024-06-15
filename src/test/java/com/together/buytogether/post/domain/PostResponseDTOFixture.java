package com.together.buytogether.post.domain;

import com.together.buytogether.post.dto.response.PostResponseDTO;

import java.time.LocalDateTime;

public class PostResponseDTOFixture {

    String memberName = "작성자 이름";
    Long postId = 1L;
    String title = "제목";
    String content = "내용";
    LocalDateTime expiredAt = LocalDateTime.now();

    public static PostResponseDTOFixture aPostResponseDTO() {
        return new PostResponseDTOFixture();
    }

    public PostResponseDTOFixture memberName(String memberName) {
        this.memberName = memberName;
        return this;
    }

    public PostResponseDTOFixture postId(Long postId) {
        this.postId = postId;
        return this;
    }

    public PostResponseDTOFixture title(String title) {
        this.title = title;
        return this;
    }

    public PostResponseDTOFixture content(String content) {
        this.content = content;
        return this;
    }

    public PostResponseDTOFixture expiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
        return this;
    }


    public PostResponseDTO build() {
        return PostResponseDTO.builder()
                .memberName(memberName)
                .postId(postId)
                .title(title)
                .content(content)
                .expiredAt(expiredAt)
                .build();
    }
}
