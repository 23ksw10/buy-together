package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.Address;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SEX;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterPostTest {
    private RegisterPost registerPost;
    private MemberRepository memberRepository;
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository = new PostRepository();
        memberRepository = Mockito.mock(MemberRepository.class);
        registerPost = new RegisterPost(postRepository, memberRepository);
    }

    @Test
    @DisplayName("게시글 등록")
    void registerPost() {
        Member member = new Member(
                "name",
                "loginId",
                "password",
                "01011112222",
                SEX.MALE,
                new Address("경기도 고양시 덕양구 화정로 27", "625동 1004호")
        );
        Mockito.when(memberRepository.findById(1L)).
                thenReturn(Optional.of(member)
                );
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
        assertThat(postRepository.findAll()).hasSize(1);

    }

}
