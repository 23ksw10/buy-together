package com.together.buytogether.postcomment.feature;

import com.together.buytogether.member.domain.MemberFixture;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.post.domain.PostFixture;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.postcomment.domain.PostComment;
import com.together.buytogether.postcomment.domain.PostCommentFixture;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


public class UpdateCommentTest {
    private UpdateComment updateComment;
    private MemberRepository memberRepository;
    private PostRepository postRepository;
    private PostCommentRepository postCommentRepository;

    @BeforeEach
    void setUp() {
        memberRepository = Mockito.mock(MemberRepository.class);
        postRepository = Mockito.mock(PostRepository.class);
        postCommentRepository = Mockito.mock(PostCommentRepository.class);
        updateComment = new UpdateComment(
                memberRepository,
                postRepository,
                postCommentRepository
        );
    }

    @Test
    @DisplayName("댓글 수정")
    void updateComment() {
        Mockito.when(memberRepository.getByMemberId(1L))
                .thenReturn(MemberFixture.aMember().build());

        Mockito.when(postRepository.getByPostId(1L))
                .thenReturn(PostFixture.aPost().build());
        PostComment postComment = PostCommentFixture.aPostComment().build();
        Mockito.when(postCommentRepository.getByCommentId(1L))
                .thenReturn(postComment);
        String content = "댓글 수정";
        Long memberId = 1L;
        Long postId = 1L;
        Long commentId = 1L;
        UpdateComment.Request request = new UpdateComment.Request(
                content
        );
        updateComment.request(memberId, postId, commentId, request);

        assertThat(postComment.getContent()).isEqualTo(content);
    }

    private class UpdateComment {
        MemberRepository memberRepository;
        PostRepository postRepository;
        PostCommentRepository postCommentRepository;

        public UpdateComment(
                MemberRepository memberRepository,
                PostRepository postRepository,
                PostCommentRepository postCommentRepository) {
            this.memberRepository = memberRepository;
            this.postRepository = postRepository;
            this.postCommentRepository = postCommentRepository;
        }

        public void request(Long memberId, Long postId, Long commentId, Request request) {
            PostComment postComment = postCommentRepository.getByCommentId(commentId);
            postComment.checkOwner(memberId);
            postComment.checkPostStatus(postId);
            postComment.update(
                    request.content,
                    LocalDateTime.now()
            );
        }

        public record Request(
                String content
        ) {
            public Request {
                Assert.hasText(content, "댓글 내용은 필수입니다.");
            }
        }
    }
}
