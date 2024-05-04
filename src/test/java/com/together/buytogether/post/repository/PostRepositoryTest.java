package com.together.buytogether.post.repository;

import com.together.buytogether.member.domain.Address;
import com.together.buytogether.member.domain.Gender;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.post.domain.PostStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Post JPA 연결 테스트")
@DataJpaTest
public class PostRepositoryTest {
    Member savedMember;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .name("name")
                .loginId("loginId")
                .password("password")
                .gender(Gender.MALE)
                .phoneNumber("010-0000-0000")
                .address(new Address("경기도", "고양시")).build();
        savedMember = memberRepository.save(member);
    }

    @Test
    @DisplayName("insert 테스트")
    void givenTestData_whenInserting_thenWorksFine() {
        Post post = postRepository.save(new Post(savedMember, "title", "content", PostStatus.OPEN, LocalDateTime.now(), 100L, 1L));
        assertThat(postRepository.count()).isEqualTo(1);
    }


    @Test
    @DisplayName("select 테스트")
    void givenTestData_whenSelecting_thenWorksFine() {
        postRepository.save(new Post(savedMember, "title", "content", PostStatus.OPEN, LocalDateTime.now(), 100L, 1L));
        List<Post> posts = postRepository.findAll();
        assertThat(posts).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("update 테스트")
    void givenTestData_whenUpdating_thenWorksFine() {

        Post post = postRepository.save(new Post(savedMember, "title", "content", PostStatus.OPEN, LocalDateTime.now(), 100L, 1L));
        post.update("newTitle", post.getContent(), post.getStatus(), post.getExpiredAt(), post.getMaxJoinCount());
        Post updatedPost = postRepository.saveAndFlush(post);

        assertThat(updatedPost).hasFieldOrPropertyWithValue("title", "newTitle");
    }
}
