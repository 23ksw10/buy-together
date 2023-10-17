package com.together.buytogether.postcomment.feature;

import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.postcomment.domain.PostComment;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
public class DeleteComment {
    private final PostCommentRepository postCommentRepository;

    public DeleteComment(PostCommentRepository postCommentRepository) {
        this.postCommentRepository = postCommentRepository;
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    @Transactional
    public void request(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable Long commentId) {
        PostComment postComment = postCommentRepository.getByCommentId(commentId);
        checkOwner(memberId, postComment);
        postCommentRepository.delete(postComment);
    }

    private void checkOwner(Long memberId, PostComment postComment) {
        if (!postComment.checkOwner(memberId)) {
            throw new IllegalArgumentException("댓글 작성자가 아닙니다.");
        }
    }
}
