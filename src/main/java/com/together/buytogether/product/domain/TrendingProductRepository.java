package com.together.buytogether.product.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface TrendingProductRepository extends Repository<Product, Long> {

	// v1: 일반적인 단일 컬럼 FK 인덱스만 사용하는 베이스라인.
	// v2용 커버링 인덱스만 제외해 MySQL이 product_id 인덱스를 선택하도록 한다.
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
		               IGNORE INDEX (idx_enroll_product_id_created_at)
		             WHERE e.product_id = p.product_id
		               AND e.created_at >= :cutoff)              AS enroll_count,
		           (SELECT COUNT(*) FROM product_like pl
		               IGNORE INDEX (idx_product_like_product_id_like_status_updated_at)
		             WHERE pl.product_id = p.product_id
		               AND pl.like_status = 'OPEN'
		               AND pl.updated_at >= :cutoff)             AS like_count
		    FROM product p
		    WHERE p.status = 'OPEN'
		) t
		ORDER BY score DESC, t.product_id ASC
		LIMIT 100
		""", nativeQuery = true)
	List<TrendingProductRow> findTrendingTop100UsingBasicIndexes(@Param("cutoff") LocalDateTime cutoff);

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
		             WHERE e.product_id = p.product_id
		               AND e.created_at >= :cutoff)              AS enroll_count,
		           (SELECT COUNT(*) FROM product_like pl
		             WHERE pl.product_id = p.product_id
		               AND pl.like_status = 'OPEN'
		               AND pl.updated_at >= :cutoff)             AS like_count
		    FROM product p
		    WHERE p.status = 'OPEN'
		) t
		ORDER BY score DESC, t.product_id ASC
		LIMIT 100
		""", nativeQuery = true)
	List<TrendingProductRow> findTrendingTop100(@Param("cutoff") LocalDateTime cutoff);
}
