package com.together.buytogether.post.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.annotations.VisibleForTesting;
import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "post")
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

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@Column(name = "expired_at", nullable = false)
	@Comment("글 만료일")
	private LocalDateTime expiredAt;

	@Builder
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
		LocalDateTime expiredAt) {
		validateUpdate(title, content, status, expiredAt);
		this.title = title;
		this.content = content;
		this.status = status;
		this.expiredAt = expiredAt;
	}

	public void deletePost() {
		if (status == PostStatus.CLOSED) {
			throw new CustomException(ErrorCode.POST_CLOSED);
		}
		status = PostStatus.CLOSED;
	}

	@VisibleForTesting
	public void setPostId(Long postId) {
		this.postId = postId;
	}
}
