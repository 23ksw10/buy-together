package com.together.buytogether.comment.feature;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberFixture;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterCommentTest {
    MemberRepository memberRepository;
    PostRepository postRepository;
    CommentRepository commentRepository;
    RegisterComment registerComment;

    @BeforeEach
    void setUp() {
        memberRepository = Mockito.mock(MemberRepository.class);
        postRepository = Mockito.mock(PostRepository.class);
        commentRepository = new CommentRepository();
        registerComment = new RegisterComment(memberRepository, postRepository, commentRepository);
    }

    @Test
    @DisplayName("댓글 등록")
    void registerComment() {
        Mockito.when(memberRepository.getByMemberId(1L)).thenReturn(
                MemberFixture.aMember().build()
        );
        Mockito.when(postRepository.getByPostId(1L)).thenReturn(
                PostFixture.aPost().build()
        );
        RegisterComment.Request request = new RegisterComment.Request(
                "댓글 내용",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Long memberId = 1L;
        Long postId = 1L;
        registerComment.request(memberId, postId, request);
        assertThat(commentRepository.findAll().size()).isEqualTo(1);
        assertThat(commentRepository.getByCommentId(1L).getContent()).isEqualTo("댓글 내용");

    }

    private static class Comment {
        private final Member member;
        private final Post post;
        private final String content;
        private final LocalDateTime createAt;
        private final LocalDateTime updateAt;
        private Long commentId;

        public Comment(
                Member member,
                Post post,
                String content,
                LocalDateTime createAt,
                LocalDateTime updateAt) {
            validateConstructor(member, post, content, createAt, updateAt);
            this.member = member;
            this.post = post;
            this.content = content;
            this.createAt = createAt;
            this.updateAt = updateAt;
        }

        private static void validateConstructor(Member member, Post post, String content, LocalDateTime createAt, LocalDateTime updateAt) {
            Assert.notNull(member, "댓글 작성자는 필수입니다.");
            Assert.notNull(post, "댓글이 달린 게시글은 필수입니다.");
            Assert.hasText(content, "댓글 내용은 필수입니다.");
            Assert.notNull(createAt, "댓글 생성일은 필수입니다.");
            Assert.notNull(updateAt, "댓글 수정일은 필수입니다.");
            if (createAt.isAfter(updateAt)) {
                throw new IllegalArgumentException("댓글 생성일은 수정일보다 빠를 수 없습니다.");
            }
        }

        public void assignId(Long commentId) {
            this.commentId = commentId;
        }

        public Long getCommentId() {
            return commentId;
        }

        public String getContent() {
            return content;
        }
    }

    private class RegisterComment {
        MemberRepository memberRepository;
        PostRepository postRepository;
        CommentRepository commentRepository;

        public RegisterComment(
                MemberRepository memberRepository,
                PostRepository postRepository,
                CommentRepository commentRepository) {
            this.memberRepository = memberRepository;
            this.postRepository = postRepository;
            this.commentRepository = commentRepository;
        }

        public void request(Long memberId, Long postId, Request request) {
            Member member = memberRepository.getByMemberId(memberId);
            Post post = postRepository.getByPostId(postId);
            Comment comment = request.toDomain(member, post);
            commentRepository.save(comment);
        }

        public record Request(
                String content,
                LocalDateTime createAt,
                LocalDateTime updateAt
        ) {
            public Request {
                Assert.hasText(content, "댓글 내용은 필수입니다.");
                Assert.notNull(createAt, "댓글 생성일은 필수입니다.");
                Assert.notNull(updateAt, "댓글 수정일은 필수입니다.");
            }

            public Comment toDomain(Member member, Post post) {
                return new Comment(
                        member,
                        post,
                        content,
                        createAt,
                        updateAt
                );
            }
        }

    }

    private class CommentRepository {
        private final Map<Long, Comment> comments = new HashMap<>();
        private Long commentId = 1L;

        public void save(Comment comment) {
            comment.assignId(commentId++);
            comments.put(comment.getCommentId(), comment);
        }

        public List<Comment> findAll() {
            return new ArrayList<>(comments.values());
        }

        public Comment getByCommentId(Long commentId) {
            return comments.get(commentId);
        }
    }
}
