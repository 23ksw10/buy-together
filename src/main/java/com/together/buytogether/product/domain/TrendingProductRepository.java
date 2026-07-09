package com.together.buytogether.product.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface TrendingProductRepository extends Repository<Product, Long> {

	@Query(value = """
		SELECT t.product_id                      AS productId,
		       t.title                           AS title,
		       t.price                           AS price,
		       t.enroll_count                    AS enrollCount,
		       t.like_count                      AS likeCount,
		       t.enroll_count * 2 + t.like_count AS score
		FROM (
		    SELECT p.product_id, p.title, p.price,
		           (SELECT COUNT(*) FROM enroll e
		             WHERE e.product_id = p.product_id)         AS enroll_count,
		           (SELECT COUNT(*) FROM product_like pl
		             WHERE pl.product_id = p.product_id
		               AND pl.like_status = 'OPEN')             AS like_count
		    FROM product p
		    WHERE p.status = 'OPEN'
		) t
		ORDER BY score DESC, t.product_id ASC
		LIMIT 10
		""", nativeQuery = true)
	List<TrendingProductRow> findTrendingTop10();
}
