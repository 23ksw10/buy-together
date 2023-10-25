package com.together.buytogether.postcomment.feature;

import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.postcomment.service.PostCommentService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
public class DeleteComment {
    private final PostCommentService postCommentService;

    public DeleteComment(PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    @Transactional
    public void request(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable Long commentId) {
        postCommentService.deleteComment(memberId, commentId);
    }

}
