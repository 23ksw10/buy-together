package com.together.buytogether.postcomment.repository;

import com.together.buytogether.member.domain.Address;
import com.together.buytogether.member.domain.Gender;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.post.domain.PostStatus;
import com.together.buytogether.postcomment.domain.PostComment;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PostComment JPA 연결 테스트")
@DataJpaTest
public class PostCommentRepositoryTest {
    @Autowired
    PostCommentRepository postCommentRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    Member savedMember;
    Post post;
    Post savePost;
    PostComment postComment;

    @BeforeEach
    void setUp() {
        savedMember = memberRepository.save(createMember());

        post = Post.builder()
                .member(savedMember)
                .expiredAt(LocalDateTime.now())
                .content("content")
                .status(PostStatus.OPEN)
                .title("title")
                .maxJoinCount(100L)
                .joinCount(1L)
                .build();
        savePost = postRepository.save(post);
        postComment = PostComment.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .member(savedMember)
                .post(post)
                .content("content")
                .build();
    }

    @Test
    @DisplayName("insert 테스트")
    void givenTestData_whenInserting_thenWorksFine() {
        postCommentRepository.save(postComment);
        assertThat(postCommentRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("select 테스트")
    void givenTestData_whenSelecting_thenWorksFine() {
        postCommentRepository.save(postComment);
        List<PostComment> postComments = postCommentRepository.findAll();
        assertThat(postComments).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("update 테스트")
    void givenTestData_whenUpdating_thenWorksFine() {

        PostComment savedPostComment = postCommentRepository.save(postComment);
        savedPostComment.update("newContent", LocalDateTime.now());
        PostComment updatePostComment = postCommentRepository.saveAndFlush(savedPostComment);

        assertThat(updatePostComment).hasFieldOrPropertyWithValue("content", "newContent");
    }

    @Test
    @DisplayName("delete 테스트")
    void givenTestData_whenDeleting_thenWorksFine() {

        PostComment savedPostComment = postCommentRepository.save(postComment);
        postCommentRepository.delete(savedPostComment);
        assertThat(postCommentRepository.count()).isEqualTo(0);
    }


    private Member createMember() {
        return Member.builder()
                .name("name")
                .password("test")
                .phoneNumber("010-0000-0000")
                .gender(Gender.MALE)
                .loginId("test-id")
                .address(new Address("경기도", "고양시"))
                .build();
    }
}
