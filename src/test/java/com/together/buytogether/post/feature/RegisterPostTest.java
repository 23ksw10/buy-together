package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

public class RegisterPostTest {
    private RegisterPost registerPost;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        registerPost = new RegisterPost();
    }

    @Test
    @DisplayName("게시글 등록")
    void registerPost() {
        Long memberId = 1L;
        String title = "title";
        String content = "content";
        LocalDateTime expiredAt = LocalDateTime.now().plusHours(10);
        RegisterPost.Request request = new RegisterPost.Request(
                memberId,
                title,
                content,
                expiredAt
        );
        registerPost.requet(request);
    }

    private class RegisterPost {
        public void requet(Request request) {

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


        }
    }

}
