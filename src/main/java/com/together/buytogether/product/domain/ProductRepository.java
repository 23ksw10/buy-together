package com.together.buytogether.product.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;

import jakarta.persistence.LockModeType;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Product p where p.productId = :productId")
	Optional<Product> findWithPessimisticByProductId(@Param("productId") Long productId);

	@Query("select p from Product p join fetch p.member")
	List<Product> findAll();

	@Query("select p from Product p join fetch p.member where p.productId = :productId")
	Optional<Product> findById(@Param("productId") Long productId);

	default Product getByProductId(Long productId) {
		return findById(productId).orElseThrow(
			() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
		);
	}
}
