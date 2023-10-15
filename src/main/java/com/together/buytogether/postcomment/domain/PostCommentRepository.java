package com.together.buytogether.postcomment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    default PostComment getByCommentId(Long commentId) {
        return findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 댓글입니다")
        );
    }

    @Query("select p from PostComment p where p.post.postId = :postId")
    List<PostComment> findAllByPostId(@Param("postId") Long postId);

}
