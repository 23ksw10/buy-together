package com.together.buytogether.postcomment.feature;

import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.postcomment.dto.request.UpdateCommentDTO;
import com.together.buytogether.postcomment.service.PostCommentService;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
public class UpdateComment {
    private final PostCommentService postCommentService;

    public UpdateComment(PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    @Transactional
    public void request(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable @Valid Long postId,
            @PathVariable @Valid Long commentId,
            @RequestBody @Valid UpdateCommentDTO updateCommentDTO) {
        postCommentService.updateComment(memberId, commentId, updateCommentDTO);
    }

}
