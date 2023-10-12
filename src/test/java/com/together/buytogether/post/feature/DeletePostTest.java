package com.together.buytogether.post.feature;

import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostFixture;
import com.together.buytogether.post.domain.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DeletePostTest {
    DeletePost deletePost;
    PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository = Mockito.mock(PostRepository.class);
        deletePost = new DeletePost(postRepository);
    }

    @Test
    @DisplayName("게시글 삭제")
    void deletePost() {
        Post post = PostFixture.aPost().build();
        Mockito.when(postRepository.getByPostId(1L))
                .thenReturn(post);
        Long postId = 1L;
        Long memberId = 1L;
        deletePost.request(memberId, postId);
        Mockito.verify(postRepository, Mockito.times(1)).delete(post);
    }

    private class DeletePost {

        PostRepository postRepository;

        public DeletePost(PostRepository postRepository) {
            this.postRepository = postRepository;
        }

        public void request(Long memberId, Long postId) {
            Post post = postRepository.getByPostId(postId);
            post.checkOwner(memberId);
            postRepository.delete(post);
        }
    }
}
