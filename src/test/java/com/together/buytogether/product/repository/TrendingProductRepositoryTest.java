package com.together.buytogether.product.repository;

import static com.together.buytogether.enroll.domain.EnrollFixture.*;
import static com.together.buytogether.member.domain.MemberFixture.*;
import static com.together.buytogether.product.domain.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.together.buytogether.config.JpaAuditingConfig;
import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductLike;
import com.together.buytogether.product.domain.ProductLikeRepository;
import com.together.buytogether.product.domain.ProductLikeStatus;
import com.together.buytogether.product.domain.ProductRepository;
import com.together.buytogether.product.domain.ProductStatus;
import com.together.buytogether.product.domain.TrendingProductRepository;
import com.together.buytogether.product.domain.TrendingProductRow;

import jakarta.persistence.EntityManager;

@DisplayName("TrendingProduct Repository 테스트")
@Testcontainers
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TrendingProductRepositoryTest {

	@Container
	private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName("buy_together_test")
		.withUsername("test")
		.withPassword("test");

	@DynamicPropertySource
	static void configureMySql(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
		registry.add("spring.datasource.username", MYSQL::getUsername);
		registry.add("spring.datasource.password", MYSQL::getPassword);
		registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
		registry.add("spring.jpa.show-sql", () -> "false");
	}

	@Autowired
	private TrendingProductRepository trendingProductRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductLikeRepository productLikeRepository;

	@Autowired
	private EnrollRepository enrollRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	@DisplayName("두 쿼리는 OPEN 상품을 점수순으로 동일하게 조회한다")
	void findTrendingProducts() {
		Member owner = saveMember("owner@example.com");
		Member firstBuyer = saveMember("buyer1@example.com");
		Member secondBuyer = saveMember("buyer2@example.com");

		Product first = saveProduct(owner, "첫 번째 상품", ProductStatus.OPEN);
		Product second = saveProduct(owner, "두 번째 상품", ProductStatus.OPEN);
		Product third = saveProduct(owner, "세 번째 상품", ProductStatus.OPEN);
		Product closed = saveProduct(owner, "종료 상품", ProductStatus.CLOSED);

		enrollRepository.saveAll(List.of(
			aEnroll().member(firstBuyer).product(first).build(),
			aEnroll().member(secondBuyer).product(first).build(),
			aEnroll().member(firstBuyer).product(second).build(),
			aEnroll().member(firstBuyer).product(closed).build(),
			aEnroll().member(secondBuyer).product(closed).build()
		));
		productLikeRepository.saveAll(List.of(
			new ProductLike(firstBuyer, first, ProductLikeStatus.OPEN),
			new ProductLike(secondBuyer, first, ProductLikeStatus.CLOSED),
			new ProductLike(firstBuyer, second, ProductLikeStatus.OPEN),
			new ProductLike(secondBuyer, second, ProductLikeStatus.OPEN),
			new ProductLike(firstBuyer, closed, ProductLikeStatus.OPEN)
		));
		flushAndClear();

		List<TrendingResult> baseline = toResults(
			trendingProductRepository.findTrendingTop100UsingBasicIndexes(sevenDaysAgo()));
		List<TrendingResult> optimized = toResults(trendingProductRepository.findTrendingTop100(sevenDaysAgo()));

		List<TrendingResult> expected = List.of(
			new TrendingResult(first.getProductId(), "첫 번째 상품", 1_000L, 2L, 1L, 5L),
			new TrendingResult(second.getProductId(), "두 번째 상품", 1_000L, 1L, 2L, 4L),
			new TrendingResult(third.getProductId(), "세 번째 상품", 1_000L, 0L, 0L, 0L)
		);
		assertThat(baseline).containsExactlyElementsOf(expected);
		assertThat(optimized).containsExactlyElementsOf(expected);
	}

	@Test
	@DisplayName("점수가 같으면 상품 번호순으로 최대 100개를 조회한다")
	void findTrendingProductsWithLimitAndTieBreaker() {
		Member owner = saveMember("limit-owner@example.com");
		List<Product> products = new ArrayList<>();
		for (int index = 0; index < 101; index++) {
			products.add(aProduct()
				.member(owner)
				.title("상품 " + index)
				.status(ProductStatus.OPEN)
				.build());
		}
		List<Product> savedProducts = productRepository.saveAll(products);
		flushAndClear();

		List<TrendingProductRow> result = trendingProductRepository.findTrendingTop100(sevenDaysAgo());

		List<Long> expectedProductIds = savedProducts.stream()
			.map(Product::getProductId)
			.sorted()
			.limit(100)
			.toList();
		assertThat(result).hasSize(100);
		assertThat(result).extracting(TrendingProductRow::getProductId)
			.containsExactlyElementsOf(expectedProductIds);
		assertThat(result).extracting(TrendingProductRow::getScore)
			.containsOnly(0L);
	}

	@Test
	@DisplayName("7일 윈도우 이전의 참여와 좋아요는 집계에서 제외된다")
	void excludeActivityOlderThanWindow() {
		Member owner = saveMember("window-owner@example.com");
		Member firstBuyer = saveMember("window-buyer1@example.com");
		Member secondBuyer = saveMember("window-buyer2@example.com");
		Product product = saveProduct(owner, "윈도우 상품", ProductStatus.OPEN);

		enrollRepository.save(aEnroll().member(firstBuyer).product(product).build());
		Enroll oldEnroll = enrollRepository.save(aEnroll().member(secondBuyer).product(product).build());
		productLikeRepository.save(new ProductLike(firstBuyer, product, ProductLikeStatus.OPEN));
		ProductLike oldLike = productLikeRepository.save(new ProductLike(secondBuyer, product, ProductLikeStatus.OPEN));
		flushAndClear();
		// @CreatedDate/@LastModifiedDate는 저장 시점으로 채워지므로 윈도우 밖 데이터는 SQL로 되돌린다
		backdate("UPDATE enroll SET created_at = ?1 WHERE enroll_id = ?2", oldEnroll.getEnrollId());
		backdate("UPDATE product_like SET updated_at = ?1 WHERE product_like_id = ?2", oldLike.getProductLikeId());

		List<TrendingProductRow> result = trendingProductRepository.findTrendingTop100(sevenDaysAgo());

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getEnrollCount()).isEqualTo(1L);
		assertThat(result.get(0).getLikeCount()).isEqualTo(1L);
		assertThat(result.get(0).getScore()).isEqualTo(3L);
	}

	private LocalDateTime sevenDaysAgo() {
		return LocalDateTime.now().minusDays(7);
	}

	private void backdate(String sql, Long id) {
		entityManager.createNativeQuery(sql)
			.setParameter(1, LocalDateTime.now().minusDays(8))
			.setParameter(2, id)
			.executeUpdate();
	}

	private Member saveMember(String email) {
		return memberRepository.save(aMember().email(email).build());
	}

	private Product saveProduct(Member owner, String title, ProductStatus status) {
		return productRepository.save(aProduct()
			.member(owner)
			.title(title)
			.status(status)
			.build());
	}

	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}

	private List<TrendingResult> toResults(List<TrendingProductRow> rows) {
		return rows.stream()
			.map(row -> new TrendingResult(
				row.getProductId(),
				row.getTitle(),
				row.getPrice(),
				row.getEnrollCount(),
				row.getLikeCount(),
				row.getScore()
			))
			.toList();
	}

	private record TrendingResult(
		Long productId,
		String title,
		Long price,
		Long enrollCount,
		Long likeCount,
		Long score
	) {
	}
}
