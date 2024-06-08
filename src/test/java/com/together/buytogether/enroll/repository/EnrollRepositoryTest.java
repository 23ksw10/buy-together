package com.together.buytogether.enroll.repository;

import com.together.buytogether.config.JpaAuditingConfig;
import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollRepository;
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

import static com.together.buytogether.enroll.domain.EnrollFixture.aEnroll;
import static com.together.buytogether.member.domain.MemberFixture.aMember;
import static com.together.buytogether.post.domain.PostFixture.aPost;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Enroll JPA 연결 테스트")
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EnrollRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EnrollRepository enrollRepository;

    Member member;
    Post post;
    Member savedMember;
    Post savedPost;

    Enroll enroll;

    @BeforeEach
    void setUp() {
        member = aMember().build();
        savedMember = memberRepository.save(member);
        post = aPost().member(member).build();
        savedPost = postRepository.save(post);
        enroll = aEnroll().member(member).post(post).build();
    }

    @Test
    @DisplayName("insert 테스트")
    public void insertEnroll() {

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
    public void deleteEnroll() {

        Enroll savedEnroll = enrollRepository.save(enroll);
        // when
        enrollRepository.delete(savedEnroll);

        // then
        assertThat(enrollRepository.count()).isEqualTo(0);

    }

    @Test
    @DisplayName("select 테스트")
    public void selectEnroll() {

        enrollRepository.saveAndFlush(enroll);

        // when
        Enroll foundEnroll = enrollRepository.getEnroll(savedMember.getMemberId(), savedPost.getPostId());

        // then
        assertThat(foundEnroll).isNotNull();
        assertThat(foundEnroll.getMember()).isEqualTo(savedMember);
        assertThat(foundEnroll.getPost()).isEqualTo(savedPost);

    }

}
