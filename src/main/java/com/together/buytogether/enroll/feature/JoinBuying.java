package com.together.buytogether.enroll.feature;

import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class JoinBuying {
    PostRepository postRepository;
    EnrollRepository enrollRepository;
    MemberRepository memberRepository;

    public JoinBuying(
            PostRepository postRepository,
            EnrollRepository enrollRepository,
            MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.enrollRepository = enrollRepository;
        this.memberRepository = memberRepository;
    }

    @PostMapping("/posts/{postId}/enroll")
    @ResponseStatus(HttpStatus.CREATED)
    public void requset(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable Long postId) {
        Post post = postRepository.getByPostId(postId);
        Member member = memberRepository.getByMemberId(memberId);
        enrollRepository.findByMemberIdAndPostId(memberId, postId).ifPresent(enroll -> {
            throw new IllegalArgumentException("이미 참여한 구매글입니다.");
        });
        Enroll enroll = new Enroll(member, post, LocalDateTime.now());
        post.increaseJoinCount();
        enrollRepository.save(enroll);
    }
}
