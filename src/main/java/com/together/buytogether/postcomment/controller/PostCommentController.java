package com.together.buytogether.postcomment.controller;

import com.together.buytogether.annotation.LoginRequired;
import com.together.buytogether.annotation.LoginUser;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.postcomment.dto.request.CommentDTO;
import com.together.buytogether.postcomment.dto.response.CommentResponseDTO;
import com.together.buytogether.postcomment.dto.response.RegisterCommentResponseDTO;
import com.together.buytogether.postcomment.dto.response.UpdateCommentResponseDTO;
import com.together.buytogether.postcomment.service.PostCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @LoginRequired
    public ResponseEntity<ResponseDTO<String>> deleteComment(
            @LoginUser Long memberId,
            @PathVariable Long commentId) {
        return ResponseEntity.status(HttpStatus.OK).body(postCommentService.deleteComment(memberId, commentId));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<CommentResponseDTO>>> getAllComment(@PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK).body(postCommentService.getPostComments(postId));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ResponseDTO<CommentResponseDTO>> getComment(@PathVariable Long commentId) {
        return ResponseEntity.status(HttpStatus.OK).body(postCommentService.getPostComment(commentId));
    }

    @PostMapping
    @LoginRequired
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseDTO<RegisterCommentResponseDTO>> registerComment(
            @LoginUser Long memberId,
            @PathVariable Long postId,
            @RequestBody @Valid CommentDTO commentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postCommentService.registerComment(memberId, postId, commentDTO));
    }

    @PutMapping("/{commentId}")
    @LoginRequired
    public ResponseEntity<ResponseDTO<UpdateCommentResponseDTO>> updateComment(
            @LoginUser Long memberId,
            @PathVariable @Valid Long commentId,
            @RequestBody @Valid CommentDTO commentDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(postCommentService.updateComment(memberId, commentId, commentDTO));
    }
}
