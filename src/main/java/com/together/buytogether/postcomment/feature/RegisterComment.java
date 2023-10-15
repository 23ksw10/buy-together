package com.together.buytogether.postcomment.feature;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.postcomment.domain.PostComment;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class RegisterComment {
    MemberRepository memberRepository;
    PostRepository postRepository;
    PostCommentRepository postCommentRepository;

    public RegisterComment(
            MemberRepository memberRepository,
            PostRepository postRepository,
            PostCommentRepository postCommentRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
    }

    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void request(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable Long postId,
            @RequestBody @Valid Request request) {
        Member member = memberRepository.getByMemberId(memberId);
        Post post = postRepository.getByPostId(postId);
        PostComment comment = request.toDomain(member, post);
        postCommentRepository.save(comment);
    }

    public record Request(
            @NotBlank(message = "댓글 내용은 필수입니다")
            String content,
            @NotNull(message = "댓글 생성일은 필수입니다")
            LocalDateTime createAt,
            @NotNull(message = "댓글 수정일은 필수입니다")
            LocalDateTime updateAt
    ) {
        public PostComment toDomain(Member member, Post post) {
            return new PostComment(
                    member,
                    post,
                    content,
                    createAt,
                    updateAt
            );
        }
    }

}
