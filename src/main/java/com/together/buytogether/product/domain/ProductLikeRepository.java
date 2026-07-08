package com.together.buytogether.product.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
	@Query("SELECT l FROM ProductLike l JOIN FETCH l.member m JOIN FETCH l.product p WHERE m.memberId = :memberId AND p.productId = :productId")
	Optional<ProductLike> findByMemberIdAndProductId(@Param("memberId") Long memberId,
		@Param("productId") Long productId);
}
