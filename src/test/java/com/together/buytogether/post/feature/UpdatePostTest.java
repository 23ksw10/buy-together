package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostFixture;
import com.together.buytogether.post.domain.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdatePostTest {
    UpdatePost updatePost;
    PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository = Mockito.mock(PostRepository.class);
        updatePost = new UpdatePost(postRepository);
    }

    @Test
    @DisplayName("게시글 수정")
    void updatePost() {
        Post post = PostFixture.aPost().build();
        Mockito.when(postRepository.getByPostId(Mockito.anyLong())).thenReturn(post);
        Long memberId = 1L;
        Long postId = 1L;
        String newTitle = "newTitle";
        String newContent = "newContent";
        UpdatePost.Request request = new UpdatePost.Request(
                newTitle,
                newContent,
                LocalDateTime.now().plusDays(2)
        );
        updatePost.request(memberId, postId, request);

        assertThat(post.getTitle()).isEqualTo(newTitle);
        assertThat(post.getContent()).isEqualTo(newContent);

    }

    private class UpdatePost {
        MemberRepository memberRepository;
        PostRepository postRepository;

        public UpdatePost(PostRepository postRepository) {
            this.postRepository = postRepository;
        }

        public void request(Long memberId, Long postId, Request request) {
            Post post = postRepository.getByPostId(postId);
            post.checkOwner(memberId);
            post.update(
                    request.title,
                    request.content,
                    request.expiredAt
            );
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
        }
    }
}
