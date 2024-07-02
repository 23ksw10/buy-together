package com.together.buytogether.post.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;

import jakarta.persistence.LockModeType;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Post p where p.postId = :postId")
	Optional<Post> findWithPessimisticByPostId(@Param("postId") Long postId);

	@Query("select p from Post p join fetch p.member")
	List<Post> findAll();

	@Query("select p from Post p join fetch p.member where p.postId = :id")
	Optional<Post> findById(@Param("id") Long ig);

	default Post getByPostId(Long postId) {
		return findById(postId).orElseThrow(
			() -> new CustomException(ErrorCode.POST_NOT_FOUND));
	}
}
