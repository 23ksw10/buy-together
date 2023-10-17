package com.together.buytogether.postcomment.feature;

import com.together.buytogether.postcomment.domain.PostComment;
import com.together.buytogether.postcomment.domain.PostCommentFixture;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DeleteCommentTest {
    private DeleteComment deleteComment;
    private PostCommentRepository postCommentRepository;

    @BeforeEach
    void setUp() {
        postCommentRepository = Mockito.mock(PostCommentRepository.class);
        deleteComment = new DeleteComment(postCommentRepository);
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment() {
        Long commentId = 1L;
        Long memberId = 1L;
        PostComment postComment = PostCommentFixture.aPostComment().build();
        Mockito.when(postCommentRepository.getByCommentId(commentId))
                .thenReturn(postComment);
        deleteComment.request(memberId, commentId);
        Mockito.verify(postCommentRepository, Mockito.times(1)).delete(postComment);
    }

    private class DeleteComment {
        private final PostCommentRepository postCommentRepository;

        public DeleteComment(PostCommentRepository postCommentRepository) {
            this.postCommentRepository = postCommentRepository;
        }

        public void request(Long memberId, Long commentId) {
            PostComment postComment = postCommentRepository.getByCommentId(commentId);
            checkOwner(memberId, postComment);
            postCommentRepository.delete(postComment);
        }

        private void checkOwner(Long memberId, PostComment postComment) {
            if (!postComment.checkOwner(memberId)) {
                throw new IllegalArgumentException("댓글 작성자가 아닙니다.");
            }
        }
    }
}
