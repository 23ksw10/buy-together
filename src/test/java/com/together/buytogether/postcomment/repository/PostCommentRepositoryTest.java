package com.together.buytogether.postcomment.repository;

import com.together.buytogether.config.JpaAuditingConfig;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.postcomment.domain.PostComment;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static com.together.buytogether.member.domain.MemberFixture.aMember;
import static com.together.buytogether.post.domain.PostFixture.aPost;
import static com.together.buytogether.postcomment.domain.PostCommentFixture.aPostComment;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PostComment JPA 연결 테스트")
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
        savedMember = memberRepository.save(aMember().build());
        post = aPost().member(savedMember).build();
        savePost = postRepository.save(post);
        postComment = aPostComment().member(savedMember).post(savePost).build();
    }

    @Test
    @DisplayName("insert 테스트")
    void insertPostComment() {
        postCommentRepository.save(postComment);
        assertThat(postCommentRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("select 테스트")
    void selectPostComment() {
        postCommentRepository.save(postComment);
        List<PostComment> postComments = postCommentRepository.findAll();
        assertThat(postComments).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("update 테스트")
    void updatePostComment() {

        PostComment savedPostComment = postCommentRepository.save(postComment);
        savedPostComment.update("newContent", LocalDateTime.now());
        PostComment updatePostComment = postCommentRepository.saveAndFlush(savedPostComment);

        assertThat(updatePostComment).hasFieldOrPropertyWithValue("content", "newContent");
    }

    @Test
    @DisplayName("delete 테스트")
    void deletePostComment() {

        PostComment savedPostComment = postCommentRepository.save(postComment);
        postCommentRepository.delete(savedPostComment);
        assertThat(postCommentRepository.count()).isEqualTo(0);
    }


}
