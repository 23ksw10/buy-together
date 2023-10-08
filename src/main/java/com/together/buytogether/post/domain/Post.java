package com.together.buytogether.post.domain;

import com.together.buytogether.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Component("포스트")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    @Comment("포스트 아이디")
    @Getter
    private Long postId;

    @Comment("회원")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "title", nullable = false)
    @Comment("글 제목")
    private String title;

    @Column(name = "content", nullable = false)
    @Comment("글 내용")
    private String content;

    @Column(name = "expired_at", nullable = false)
    @Comment("글 만료일")
    private LocalDateTime expiredAt;

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


}
