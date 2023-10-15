package com.together.buytogether.postcomment.feature;

import com.together.buytogether.postcomment.domain.PostComment;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
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
    public List<CommentResponse> getAllComment(@PathVariable Long postId) {
        List<PostComment> comments = postCommentRepository.findAllByPostId(postId);
        return comments.stream()
                .map(c -> new CommentResponse(
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
    public CommentResponse getComment(@PathVariable Long postId, @PathVariable Long commentId) {
        PostComment comment = postCommentRepository.getByCommentId(commentId);
        return new CommentResponse(
                comment.getCommentId(),
                comment.getPost().getPostId(),
                comment.getMember().getName(),
                comment.getContent(),
                comment.getCreatedAt().toString(),
                comment.getUpdatedAt().toString()
        );
    }

    public record CommentResponse(
            Long commentId,
            Long postId,
            String memberName,
            String content,
            String createdAt,
            String updatedAt
    ) {
    }

}
