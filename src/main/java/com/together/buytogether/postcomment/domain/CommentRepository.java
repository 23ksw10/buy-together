package com.together.buytogether.postcomment.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<PostComment, Long> {
    default PostComment getByCommentId(Long commentId) {
        return findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 댓글입니다")
        );
    }
}
