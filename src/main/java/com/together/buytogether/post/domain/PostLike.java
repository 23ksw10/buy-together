package com.together.buytogether.post.domain;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike {
	@Id
	@Column(name = "post_like_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	private Long postLikeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@Column(name = "like_status", nullable = false)
	@Enumerated(EnumType.STRING)
	@Getter
	private PostLikeStatus postLikeStatus;

	@Builder
	public PostLike(Member member, Post post, PostLikeStatus postLikeStatus) {
		this.member = member;
		this.post = post;
		this.postLikeStatus = postLikeStatus;
	}

	public void delete() {
		this.postLikeStatus = PostLikeStatus.CLOSED;
	}

	public void active() {
		this.postLikeStatus = PostLikeStatus.OPEN;
	}

	public void changeStatus() {
		if (this.postLikeStatus == PostLikeStatus.OPEN) {
			this.postLikeStatus = PostLikeStatus.CLOSED;
		} else {
			this.postLikeStatus = PostLikeStatus.OPEN;
		}
	}
}
