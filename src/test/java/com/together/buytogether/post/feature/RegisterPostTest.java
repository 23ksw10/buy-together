package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.Address;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SEX;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.*;

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
        assertThat(postRepository.findAll()).isEqualTo(1);

    }

    public static class Post {
        private final Member member;
        private final String title;
        private final String content;
        private final LocalDateTime expiredAt;
        private Long id;

        public Post(
                Member member,
                String title,
                String content,
                LocalDateTime expiredAt) {
            validateConstructor(
                    member,
                    title,
                    content,
                    expiredAt);

            this.member = member;
            this.title = title;
            this.content = content;
            this.expiredAt = expiredAt;


        }

        private static void validateConstructor(
                Member member,
                String title,
                String content,
                LocalDateTime expiredAt) {
            Assert.notNull(member, "회원 정보는 필수입니다");
            Assert.hasText(title, "글 제목은 필수입니다");
            Assert.hasText(content, "글 내용은 필수입니다");
            Assert.notNull(expiredAt, "글 만료일은 필수입니다");
        }

        public Long getId() {
            return id;
        }

        public void assingId(Long id) {
            this.id = id;
        }
    }

    private class RegisterPost {
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

    private class PostRepository {
        private final Map<Long, Post> posts = new HashMap<>();
        private Long id = 1L;

        public void save(Post post) {
            post.assingId(id++);
            posts.put(post.getId(), post);
        }

        public List<Post> findAll() {
            return new ArrayList<>(posts.values());
        }
    }
}
