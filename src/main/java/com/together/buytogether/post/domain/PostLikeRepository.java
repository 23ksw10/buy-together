package com.together.buytogether.post.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
	@Query("SELECT l FROM PostLike l JOIN FETCH l.member m JOIN FETCH l.post p WHERE m.memberId = :memberId AND p.postId = :postId")
	Optional<PostLike> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);
}
