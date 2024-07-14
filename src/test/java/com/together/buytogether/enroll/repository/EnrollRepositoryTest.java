package com.together.buytogether.enroll.repository;

import static com.together.buytogether.enroll.domain.EnrollFixture.*;
import static com.together.buytogether.member.domain.MemberFixture.*;
import static com.together.buytogether.post.domain.PostFixture.*;
import static com.together.buytogether.post.domain.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.together.buytogether.config.JpaAuditingConfig;
import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.post.domain.Product;
import com.together.buytogether.post.domain.ProductRepository;

@DisplayName("Enroll JPA 연결 테스트")
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EnrollRepositoryTest {

	Member member;
	Post post;
	Member savedMember;
	Post savedPost;
	Product product;
	Enroll enroll;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private EnrollRepository enrollRepository;
	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	void setUp() {
		member = aMember().build();
		savedMember = memberRepository.save(member);
		post = aPost().member(member).build();
		savedPost = postRepository.save(post);
		product = productRepository.save(aProduct().post(savedPost).build());
		enroll = aEnroll().member(member).product(product).build();
	}

	@Test
	@DisplayName("insert 테스트")
	public void insertEnroll() {
		// when
		Enroll savedEnroll = enrollRepository.saveAndFlush(enroll);

		// then
		assertThat(enrollRepository.count()).isEqualTo(1);
		assertThat(savedEnroll.getEnrollId()).isNotNull();
		assertThat(savedEnroll.getMember()).isEqualTo(member);
		assertThat(savedEnroll.getProduct()).isEqualTo(product);
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

		Enroll savedEnroll = enrollRepository.saveAndFlush(enroll);

		// when
		Enroll foundEnroll = enrollRepository.getEnroll(savedEnroll.getEnrollId());

		// then
		assertThat(foundEnroll).isNotNull();
		assertThat(foundEnroll.getMember()).isEqualTo(savedMember);
		assertThat(foundEnroll.getProduct()).isEqualTo(product);
	}
}
