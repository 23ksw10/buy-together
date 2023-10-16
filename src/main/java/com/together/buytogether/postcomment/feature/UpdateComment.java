package com.together.buytogether.postcomment.feature;

import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.postcomment.domain.PostComment;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class UpdateComment {
    MemberRepository memberRepository;
    PostRepository postRepository;
    PostCommentRepository postCommentRepository;

    public UpdateComment(
            MemberRepository memberRepository,
            PostRepository postRepository,
            PostCommentRepository postCommentRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    @Transactional
    public void request(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable @Valid Long postId,
            @PathVariable @Valid Long commentId,
            @RequestBody @Valid Request request) {
        PostComment postComment = postCommentRepository.getByCommentId(commentId);
        postComment.checkOwner(memberId);
        postComment.checkPostStatus(postId);
        postComment.update(
                request.content,
                LocalDateTime.now()
        );
    }

    public record Request(
            @NotBlank(message = "댓글 내용은 필수입니다")
            String content
    ) {
    }
}
