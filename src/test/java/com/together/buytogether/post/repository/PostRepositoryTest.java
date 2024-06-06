package com.together.buytogether.post.repository;

import com.together.buytogether.config.JpaAuditingConfig;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.together.buytogether.member.domain.MemberFixture.aMember;
import static com.together.buytogether.post.domain.PostFixture.aPost;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Post JPA 연결 테스트")
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {
    Member savedMember;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Post post;

    @BeforeEach
    void setUp() {
        Member member = aMember().build();
        savedMember = memberRepository.save(member);
        post = aPost().member(member).build();
    }

    @Test
    @DisplayName("insert 테스트")
    void insertPost() {
        postRepository.save(post);
        assertThat(postRepository.count()).isEqualTo(1);
    }


    @Test
    @DisplayName("select 테스트")
    void selectPost() {
        postRepository.save(post);
        List<Post> posts = postRepository.findAll();
        assertThat(posts).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("update 테스트")
    void updatePost() {

        Post savedPost = postRepository.save(post);
        savedPost.update("newTitle", post.getContent(), post.getStatus(), post.getExpiredAt(), post.getMaxJoinCount());
        Post updatedPost = postRepository.saveAndFlush(savedPost);

        assertThat(updatedPost).hasFieldOrPropertyWithValue("title", "newTitle");
    }

    @Test
    @DisplayName("delete 테스트")
    void deletePost() {

        postRepository.save(post);
        assertThat(postRepository.count()).isEqualTo(1);

        postRepository.delete(post);
        assertThat(postRepository.count()).isEqualTo(0);
    }
}
