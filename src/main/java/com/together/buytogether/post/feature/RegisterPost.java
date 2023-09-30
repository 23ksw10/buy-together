package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.member.domain.SessionManager;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class RegisterPost {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    private final SessionManager sessionManger;

    public RegisterPost(
            PostRepository postRepository,
            MemberRepository memberRepository,
            SessionManager sessionManger) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.sessionManger = sessionManger;
    }

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public void requet(@RequestBody Request request,
                       HttpServletRequest httpServletRequest) {
        Cookie cookie = httpServletRequest.getCookies()[0];
        Long memberId = (Long) sessionManger.getSession(cookie.getName()).getAttribute(SessionConst.LOGIN_MEMBER);
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        Post post = request.toDomain(member);
        postRepository.save(post);
    }

    public record Request(
            String title,
            String content,
            LocalDateTime expiredAt) {
        public Request {
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
