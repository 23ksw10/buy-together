package com.together.buytogether.postcomment.domain;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Comment("댓글")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    @Comment("댓글 ID")
    private Long commentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    @Column(name = "content", nullable = false)
    @Comment("댓글 내용")
    private String content;
    @Column(name = "created_at", nullable = false)
    @Comment("댓글 생성일")
    private LocalDateTime createdAt;
    @Column(name = "update_at", nullable = false)
    @Comment("댓글 수정일")
    private LocalDateTime updatedAt;

    public PostComment(
            Member member,
            Post post,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        validateConstructor(member, post, content, createdAt, updatedAt);
        this.member = member;
        this.post = post;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private static void validateConstructor(Member member, Post post, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        Assert.notNull(member, "댓글 작성자는 필수입니다.");
        Assert.notNull(post, "댓글이 달린 게시글은 필수입니다.");
        Assert.hasText(content, "댓글 내용은 필수입니다.");
        Assert.notNull(createdAt, "댓글 생성일은 필수입니다.");
        Assert.notNull(updatedAt, "댓글 수정일은 필수입니다.");
        if (createdAt.isAfter(updatedAt)) {
            throw new IllegalArgumentException("댓글 생성일은 수정일보다 빠를 수 없습니다.");
        }
    }

    public boolean checkOwner(Long memberId) {
        return this.member.getMemberId().equals(memberId);
    }

    public void checkPostStatus() {
        if (this.post.getStatus().equals(PostStatus.CLOSED)) {
            throw new CustomException(ErrorCode.POST_CLOSED);
        }
    }

    public void update(String content, LocalDateTime now) {
        checkPostStatus();
        this.content = content;
        this.updatedAt = now;
    }
}
