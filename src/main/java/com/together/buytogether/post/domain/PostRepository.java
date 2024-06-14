package com.together.buytogether.post.domain;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface PostRepository extends JpaRepository<Post, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Post p where p.postId = :postId")
    Optional<Post> findWithPessimisticByPostId(@Param("postId") Long postId);

    default Post getByPostId(Long postId) {
        return findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}
