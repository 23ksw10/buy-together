package com.together.buytogether.postcomment.feature;

import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.postcomment.dto.request.RegisterCommentDTO;
import com.together.buytogether.postcomment.service.PostCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegisterComment {

    private final PostCommentService postCommentService;

    public RegisterComment(PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
    }

    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void request(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable Long postId,
            @RequestBody @Valid RegisterCommentDTO registerCommentDTO) {
        postCommentService.registerComment(memberId, postId, registerCommentDTO);
    }

}
