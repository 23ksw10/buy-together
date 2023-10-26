package com.together.buytogether.postcomment.domain;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    default PostComment getByCommentId(Long commentId) {
        return findById(commentId).orElseThrow(
                () -> new CustomException(ErrorCode.COMMENT_NOT_FOUND)
        );
    }

    @Query("select p from PostComment p where p.post.postId = :postId")
    List<PostComment> findAllByPostId(@Param("postId") Long postId);

}
