package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

class RegisterPost {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public RegisterPost(PostRepository postRepository, MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    public void requet(Request request) {
        Member member = memberRepository.findById(request.memberId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        Post post = request.toDomain(member);
        postRepository.save(post);
    }

    public record Request(
            Long memberId,
            String title,
            String content,
            LocalDateTime expiredAt) {
        public Request {
            Assert.notNull(memberId, "회원 번호는 필수입니다");
            Assert.hasText(title, "글 제목은 필수입니다");
            Assert.hasText(content, "글 내용은 필수입니다");
            Assert.notNull(expiredAt, "글 만료일은 필수입니다");
        }


        public Post toDomain(Member member) {

            return new Post(
                    member,
                    title,
                    content,
                    expiredAt
            );
        }
    }
}
