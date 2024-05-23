package com.together.buytogether.post.domain;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberFixture;

import java.time.LocalDateTime;

public class PostFixture {
    private Member member = MemberFixture.aMember().build();
    private String title = "title";
    private String content = "content";

    private PostStatus status = PostStatus.OPEN;
    private LocalDateTime expiredAt = LocalDateTime.now().plusDays(1);
    private Long maxJoinCount = 100L;
    private Long joinCount = 1L;

    public static PostFixture aPost() {
        return new PostFixture();
    }

    public PostFixture member(Member member) {
        this.member = member;
        return this;
    }


    public PostFixture title(String title) {
        this.title = title;
        return this;
    }

    public PostFixture content(String content) {
        this.content = content;
        return this;
    }

    public PostFixture status(PostStatus status) {
        this.status = status;
        return this;
    }

    public PostFixture expiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
        return this;
    }

    public PostFixture maxJoinCount(Long maxJoinCount) {
        this.maxJoinCount = maxJoinCount;
        return this;
    }

    public PostFixture joinCount(Long joinCount) {
        this.joinCount = joinCount;
        return this;
    }

    public PostFixture setMemberId(Long memberId) {
        this.member.setId(memberId);
        return this;
    }

    public Post build() {
        return new Post(
                member,
                title,
                content,
                status,
                expiredAt,
                maxJoinCount,
                joinCount
        );
    }
}