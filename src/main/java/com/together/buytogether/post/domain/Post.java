package com.together.buytogether.post.domain;

import com.together.buytogether.member.domain.Member;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

public class Post {
    private final Member member;
    private final String title;
    private final String content;
    private final LocalDateTime expiredAt;
    private Long id;

    public Post(
            Member member,
            String title,
            String content,
            LocalDateTime expiredAt) {
        validateConstructor(
                member,
                title,
                content,
                expiredAt);

        this.member = member;
        this.title = title;
        this.content = content;
        this.expiredAt = expiredAt;


    }

    private static void validateConstructor(
            Member member,
            String title,
            String content,
            LocalDateTime expiredAt) {
        Assert.notNull(member, "회원 정보는 필수입니다");
        Assert.hasText(title, "글 제목은 필수입니다");
        Assert.hasText(content, "글 내용은 필수입니다");
        Assert.notNull(expiredAt, "글 만료일은 필수입니다");
    }

    public Long getId() {
        return id;
    }

    public void assignId(Long id) {
        this.id = id;
    }
}
