package com.together.buytogether.postcomment.feature;

import com.together.buytogether.postcomment.dto.response.CommentResponseDTO;
import com.together.buytogether.postcomment.service.PostCommentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetComment {

    private final PostCommentService postCommentService;

    public GetComment(PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
    }

    @GetMapping("posts/{postId}/comments")
    public List<CommentResponseDTO> getAllComment(@PathVariable Long postId) {
        return postCommentService.getPostComments(postId);
    }

    @GetMapping("posts/{postId}/comments/{commentId}")
    public CommentResponseDTO getComment(@PathVariable Long postId, @PathVariable Long commentId) {
        return postCommentService.getPostComment(commentId);
    }

}
