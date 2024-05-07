package com.together.buytogether.enroll.repository;

import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.member.domain.Address;
import com.together.buytogether.member.domain.Gender;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.post.domain.PostStatus;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Enroll JPA 연결 테스트")
@DataJpaTest
public class EnrollRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EnrollRepository enrollRepository;

    Member member;
    Post post;

    @BeforeEach
    void setUp() {
        member = createMember();
        memberRepository.save(member);
        post = createPostDto().toDomain(member);
        postRepository.save(post);
    }

    @Test
    @DisplayName("insert 테스트")
    public void givenValidEnroll_whenSaving_thenSaveSuccessfully() {
        Enroll enroll = Enroll.builder()
                .member(member)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        Enroll savedEnroll = enrollRepository.save(enroll);

        // then
        assertThat(enrollRepository.count()).isEqualTo(1);
        assertThat(savedEnroll.getEnrollId()).isNotNull();
        assertThat(savedEnroll.getMember()).isEqualTo(member);
        assertThat(savedEnroll.getPost()).isEqualTo(post);
    }

    @Test
    @DisplayName("delete 테스트")
    public void givenValidEnroll_whenDeleting_thenDeleteSuccessfully() {
        Enroll enroll = Enroll.builder()
                .member(member)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
        Enroll savedEnroll = enrollRepository.save(enroll);
        // when
        enrollRepository.delete(savedEnroll);

        // then
        assertThat(enrollRepository.count()).isEqualTo(0);

    }

    @Test
    @DisplayName("select 테스트")
    public void givenMemberIdAndPostId_whenSelecting_thenSelectSuccessfully() {
        Enroll enroll = Enroll.builder()
                .member(member)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
        enrollRepository.save(enroll);

        // when
        Optional<Enroll> foundEnroll = enrollRepository.findByMemberIdAndPostId(1L, 1L);

        // then
        assertThat(foundEnroll).isPresent();
        assertThat(foundEnroll.get().getMember()).isEqualTo(member);
        assertThat(foundEnroll.get().getPost()).isEqualTo(post);

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


    private RegisterPostDTO createPostDto() {
        return RegisterPostDTO.builder()
                .title("title")
                .content("content")
                .maxJoinCount(100L)
                .joinCount(1L)
                .status(PostStatus.OPEN)
                .expiredAt(LocalDateTime.now())
                .build();
    }
}
