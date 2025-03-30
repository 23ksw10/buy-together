package com.together.buytogether.post.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	@Query("SELECT p FROM Product p JOIN FETCH p.post post JOIN FETCH post.member WHERE p.productId = :productId")
	Optional<Product> findById(@Param("productId") Long productId);

	default Product getByProductId(Long productId) {
		return findById(productId).orElseThrow(
			() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
		);
	}
}
