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
@Getter
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

    @Comment("회원 ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "title", nullable = false)
    @Comment("글 제목")
    private String title;

    @Column(name = "content", nullable = false)
    @Comment("글 내용")
    private String content;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("글 상태")
    private PostStatus status;

    @Column(name = "expired_at", nullable = false)
    @Comment("글 만료일")
    private LocalDateTime expiredAt;

    public Post(
            Member member,
            String title,
            String content,
            PostStatus status,
            LocalDateTime expiredAt) {
        validateConstructor(
                member,
                title,
                content,
                status,
                expiredAt);

        this.member = member;
        this.title = title;
        this.content = content;
        this.status = status;
        this.expiredAt = expiredAt;
    }

    private static void validateConstructor(
            Member member,
            String title,
            String content,
            PostStatus status,
            LocalDateTime expiredAt) {
        Assert.notNull(member, "회원 정보는 필수입니다");
        Assert.hasText(title, "글 제목은 필수입니다");
        Assert.hasText(content, "글 내용은 필수입니다");
        Assert.notNull(status, "글 상태는 필수입니다");
        Assert.notNull(expiredAt, "글 만료일은 필수입니다");
    }


    public void checkOwner(Long memberId) {
        if (!memberId.equals(this.member.getMemberId())) {
            throw new IllegalArgumentException("작성자가 아닙니다");
        }
    }

    private void validateUpdate(String title, String content, PostStatus status, LocalDateTime expiredAt) {
        validateStatusWhenUpdate();
        Assert.hasText(title, "글 제목은 필수입니다");
        Assert.hasText(content, "글 내용은 필수입니다");
        Assert.notNull(expiredAt, "글 만료일은 필수입니다");
    }

    private void validateStatusWhenUpdate() {
        if (status == PostStatus.CLOSED) {
            throw new IllegalStateException("종료된 게시글은 수정할 수 없습니다");
        }
    }

    public void update(
            String title,
            String content,
            PostStatus status,
            LocalDateTime expiredAt) {
        validateUpdate(title, content, status, expiredAt);
        this.title = title;
        this.content = content;
        this.status = status;
        this.expiredAt = expiredAt;
    }
}
