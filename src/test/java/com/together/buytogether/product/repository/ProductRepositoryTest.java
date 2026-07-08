package com.together.buytogether.product.repository;

import static com.together.buytogether.member.domain.MemberFixture.*;
import static com.together.buytogether.product.domain.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.together.buytogether.config.JpaAuditingConfig;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductRepository;

@DisplayName("Product JPA 연결 테스트")
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTest {
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private MemberRepository memberRepository;
	private Product product;
	private Member savedMember;

	@BeforeEach
	void setUp() {
		Member member = aMember().build();
		savedMember = memberRepository.save(member);
		product = aProduct().member(savedMember).build();
	}

	@Test
	@DisplayName("insert 테스트")
	void insertProduct() {
		productRepository.save(product);
		assertThat(productRepository.count()).isEqualTo(1);
	}

	@Test
	@DisplayName("select 테스트")
	void selectProduct() {
		productRepository.save(product);
		List<Product> products = productRepository.findAll();
		assertThat(products).isNotNull().hasSize(1);
	}

	@Test
	@DisplayName("update 테스트")
	void updateProduct() {
		Product savedProduct = productRepository.save(product);
		savedProduct.update(
			"newTitle",
			product.getContent(),
			product.getStatus(),
			product.getExpiredAt(),
			product.getPrice(),
			product.getMaxQuantity());
		Product updatedProduct = productRepository.saveAndFlush(savedProduct);

		assertThat(updatedProduct).hasFieldOrPropertyWithValue("title", "newTitle");
	}

	@Test
	@DisplayName("delete 테스트")
	void deleteProduct() {
		productRepository.save(product);
		assertThat(productRepository.count()).isEqualTo(1);

		productRepository.delete(product);
		assertThat(productRepository.count()).isEqualTo(0);
	}
}
