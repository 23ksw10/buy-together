package com.together.buytogether.productcomment.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;

public interface ProductCommentRepository extends JpaRepository<ProductComment, Long> {
	default ProductComment getByCommentId(Long commentId) {
		return findById(commentId).orElseThrow(
			() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND)
		);
	}

	@Query("select p from ProductComment p where p.product.productId = :productId")
	List<ProductComment> findAllByProductId(@Param("productId") Long productId);
}
