package com.together.buytogether.postcomment.feature;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.postcomment.domain.PostComment;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import com.together.buytogether.postcomment.dto.request.RegisterCommentDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody @Valid RegisterCommentDTO registerCommentDTO) {
        Member member = memberRepository.getByMemberId(memberId);
        Post post = postRepository.getByPostId(postId);
        PostComment comment = registerCommentDTO.toDomain(member, post);
        postCommentRepository.save(comment);
    }

}
