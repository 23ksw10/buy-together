package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.member.domain.SessionManager;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.post.domain.PostStatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
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
    public void requet(@RequestBody @Valid Request request,
                       HttpServletRequest httpServletRequest) {
        Cookie cookie = httpServletRequest.getCookies()[0];
        Long memberId = (Long) sessionManger.getSession(cookie.getName()).getAttribute(SessionConst.LOGIN_MEMBER);
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        Post post = request.toDomain(member);
        postRepository.save(post);
    }

    public record Request(
            @NotBlank(message = "글 제목은 필수입니다")
            String title,
            @NotBlank(message = "글 내용은 필수입니다")
            String content,

            @NotNull(message = "글 상태는 필수입니다")
            PostStatus status,
            @NotNull(message = "글 만료일은 필수입니다")
            LocalDateTime expiredAt) {


        public Post toDomain(Member member) {

            return new Post(
                    member,
                    title,
                    content,
                    status,
                    expiredAt
            );
        }
    }
}
