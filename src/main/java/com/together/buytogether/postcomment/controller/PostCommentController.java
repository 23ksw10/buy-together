package com.together.buytogether.postcomment.controller;

import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.postcomment.dto.request.RegisterCommentDTO;
import com.together.buytogether.postcomment.dto.request.UpdateCommentDTO;
import com.together.buytogether.postcomment.dto.response.CommentResponseDTO;
import com.together.buytogether.postcomment.service.PostCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
public class PostCommentController {
    private final PostCommentService postCommentService;

    public PostCommentController(PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable Long commentId) {
        postCommentService.deleteComment(memberId, commentId);
    }

    @GetMapping
    public List<CommentResponseDTO> getAllComment(@PathVariable Long postId) {
        return postCommentService.getPostComments(postId);
    }

    @GetMapping("/{commentId}")
    public CommentResponseDTO getComment(@PathVariable Long commentId) {
        return postCommentService.getPostComment(commentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registerComment(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable Long postId,
            @RequestBody @Valid RegisterCommentDTO registerCommentDTO) {
        postCommentService.registerComment(memberId, postId, registerCommentDTO);
    }

    @PutMapping("/{commentId}")
    public void updateComment(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable @Valid Long commentId,
            @RequestBody @Valid UpdateCommentDTO updateCommentDTO) {
        postCommentService.updateComment(memberId, commentId, updateCommentDTO);
    }
}
