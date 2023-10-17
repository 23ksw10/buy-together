package com.together.buytogether.postcomment.feature;

import com.together.buytogether.postcomment.domain.PostComment;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import com.together.buytogether.postcomment.dto.response.CommentResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetComment {


    PostCommentRepository postCommentRepository;

    public GetComment(PostCommentRepository postCommentRepository) {
        this.postCommentRepository = postCommentRepository;
    }

    @GetMapping("posts/{postId}/comments")
    public List<CommentResponseDTO> getAllComment(@PathVariable Long postId) {
        List<PostComment> comments = postCommentRepository.findAllByPostId(postId);
        return comments.stream()
                .map(c -> new CommentResponseDTO(
                        c.getCommentId(),
                        c.getPost().getPostId(),
                        c.getMember().getName(),
                        c.getContent(),
                        c.getCreatedAt().toString(),
                        c.getUpdatedAt().toString()
                ))
                .toList();
    }

    @GetMapping("posts/{postId}/comments/{commentId}")
    public CommentResponseDTO getComment(@PathVariable Long postId, @PathVariable Long commentId) {
        PostComment comment = postCommentRepository.getByCommentId(commentId);
        return new CommentResponseDTO(
                comment.getCommentId(),
                comment.getPost().getPostId(),
                comment.getMember().getName(),
                comment.getContent(),
                comment.getCreatedAt().toString(),
                comment.getUpdatedAt().toString()
        );
    }

}
