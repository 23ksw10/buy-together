package com.together.buytogether.enroll.repository;

import static com.together.buytogether.enroll.domain.EnrollFixture.*;
import static com.together.buytogether.member.domain.MemberFixture.*;
import static com.together.buytogether.product.domain.ProductFixture.*;
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
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductRepository;

@DisplayName("Enroll JPA 연결 테스트")
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EnrollRepositoryTest {

	Member member;
	Member savedMember;
	Product product;
	Enroll enroll;

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private EnrollRepository enrollRepository;

	@BeforeEach
	void setUp() {
		member = aMember().build();
		savedMember = memberRepository.save(member);
		product = productRepository.save(aProduct().member(savedMember).build());
		enroll = aEnroll().member(savedMember).product(product).build();
	}

	@Test
	@DisplayName("insert 테스트")
	public void insertEnroll() {
		Enroll savedEnroll = enrollRepository.saveAndFlush(enroll);

		assertThat(enrollRepository.count()).isEqualTo(1);
		assertThat(savedEnroll.getEnrollId()).isNotNull();
		assertThat(savedEnroll.getMember()).isEqualTo(savedMember);
		assertThat(savedEnroll.getProduct()).isEqualTo(product);
	}

	@Test
	@DisplayName("delete 테스트")
	public void deleteEnroll() {
		Enroll savedEnroll = enrollRepository.save(enroll);

		enrollRepository.delete(savedEnroll);

		assertThat(enrollRepository.count()).isEqualTo(0);
	}

	@Test
	@DisplayName("select 테스트")
	public void selectEnroll() {
		Enroll savedEnroll = enrollRepository.saveAndFlush(enroll);

		Enroll foundEnroll = enrollRepository.getEnroll(savedEnroll.getEnrollId());

		assertThat(foundEnroll).isNotNull();
		assertThat(foundEnroll.getMember()).isEqualTo(savedMember);
		assertThat(foundEnroll.getProduct()).isEqualTo(product);
	}
}
