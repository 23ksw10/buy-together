package com.together.buytogether.post.domain;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(name = "max_join_count", nullable = false)
    @Comment("최대 구매 참여 인원")
    @Min(value = 1, message = "최대 구매 참여 인원은 1명 이상이어야 합니다")
    private Long maxJoinCount;

    @Column(name = "join_count", nullable = false)
    @Comment("구매 참여 인원")
    @Min(value = 0, message = "구매 참여 인원은 0명 이상이어야 합니다")
    private Long joinCount;

    @Builder
    public Post(
            Member member,
            String title,
            String content,
            PostStatus status,
            LocalDateTime expiredAt,
            Long maxJoinCount,
            Long joinCount) {
        validateConstructor(
                member,
                title,
                content,
                status,
                expiredAt,
                maxJoinCount,
                joinCount);

        this.member = member;
        this.title = title;
        this.content = content;
        this.status = status;
        this.expiredAt = expiredAt;
        this.maxJoinCount = maxJoinCount;
        this.joinCount = joinCount;
    }

    private static void validateConstructor(
            Member member,
            String title,
            String content,
            PostStatus status,
            LocalDateTime expiredAt,
            Long maxJoinCount,
            Long joinCount) {
        Assert.notNull(member, "회원 정보는 필수입니다");
        Assert.hasText(title, "글 제목은 필수입니다");
        Assert.hasText(content, "글 내용은 필수입니다");
        Assert.notNull(status, "글 상태는 필수입니다");
        Assert.notNull(expiredAt, "글 만료일은 필수입니다");
        Assert.notNull(maxJoinCount, "최대 구매 참여 인원은 필수입니다");
        if (maxJoinCount < 1) {
            throw new IllegalArgumentException("최대 구매 참여 인원은 1명 이상이어야 합니다");
        }
        Assert.notNull(joinCount, "구매 참여 인원은 필수입니다");
        if (joinCount < 0) {
            throw new IllegalArgumentException("구매 참여 인원은 0명 이상이어야 합니다");
        }
    }


    public void checkOwner(Long memberId) {
        if (!memberId.equals(this.member.getMemberId())) {
            throw new CustomException(ErrorCode.IS_NOT_OWNER);
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
            throw new CustomException(ErrorCode.POST_CLOSED);
        }
    }

    public void update(
            String title,
            String content,
            PostStatus status,
            LocalDateTime expiredAt,
            Long maxJoinCount) {
        validateUpdate(title, content, status, expiredAt);
        this.title = title;
        this.content = content;
        this.status = status;
        this.expiredAt = expiredAt;
        this.maxJoinCount = maxJoinCount;
    }

    public void increaseJoinCount() {
        if (joinCount >= maxJoinCount) {
            throw new CustomException(ErrorCode.GATHERING_FINISHED);
        }
        joinCount++;
    }

    public void decreaseJoinCount() {
        if (joinCount <= 0) {
            throw new CustomException(ErrorCode.ENROLL_MEMBER_NOT_BE_NEGATIVE);
        }
        joinCount--;
    }
}
