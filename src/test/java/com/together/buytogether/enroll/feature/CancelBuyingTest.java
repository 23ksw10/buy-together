package com.together.buytogether.enroll.feature;

import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollFixture;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostFixture;
import com.together.buytogether.post.domain.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CancelBuyingTest {
    private CancelBuying cancelBuying;
    private EnrollRepository enrollRepository;
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        enrollRepository = Mockito.mock(EnrollRepository.class);
        postRepository = Mockito.mock(PostRepository.class);
        cancelBuying = new CancelBuying(enrollRepository, postRepository);
    }

    @Test
    @DisplayName("구매 취소")
    void cancelBuying() {
        Long memberId = 1L;
        Long postId = 1L;
        PostFixture postFixture = PostFixture.aPost().maxJoinCount(20L).joinCount(1L);
        Enroll enroll = EnrollFixture.aEnroll().postFixture(postFixture).build();
        Mockito.when(enrollRepository.getEnroll(memberId, postId))
                .thenReturn(enroll);
        Mockito.when(postRepository.getByPostId(1L))
                .thenReturn(postFixture.build());

        cancelBuying.request(memberId, postId);

        Mockito.verify(enrollRepository, Mockito.times(1))
                .delete(enroll);
    }

    private class CancelBuying {
        private final EnrollRepository enrollRepository;

        private final PostRepository postRepository;

        public CancelBuying(EnrollRepository enrollRepository, PostRepository postRepository) {
            this.enrollRepository = enrollRepository;
            this.postRepository = postRepository;
        }

        public void request(Long memberId, Long postId) {
            Post post = postRepository.getByPostId(postId);
            Enroll enroll = enrollRepository.getEnroll(memberId, postId);
            post.decreaseJoinCount();
            enrollRepository.delete(enroll);
        }

    }
}
