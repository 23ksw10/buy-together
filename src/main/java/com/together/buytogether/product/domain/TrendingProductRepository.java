package com.together.buytogether.product.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface TrendingProductRepository extends Repository<Product, Long> {

	// v1 데모용: USE INDEX ()는 "보조 인덱스를 사용하지 않음"을 의미하는 MySQL 문법으로,
	// v2에서 인덱스가 추가된 후에도 인덱스 도입 전 실행 계획을 그대로 재현한다
	@Query(value = """
		SELECT t.product_id                      AS productId,
		       t.title                           AS title,
		       t.price                           AS price,
		       t.enroll_count                    AS enrollCount,
		       t.like_count                      AS likeCount,
		       t.enroll_count * 2 + t.like_count AS score
		FROM (
		    SELECT p.product_id, p.title, p.price,
		           (SELECT COUNT(*) FROM enroll e USE INDEX ()
		             WHERE e.product_id = p.product_id)         AS enroll_count,
		           (SELECT COUNT(*) FROM product_like pl USE INDEX ()
		             WHERE pl.product_id = p.product_id
		               AND pl.like_status = 'OPEN')             AS like_count
		    FROM product p
		    WHERE p.status = 'OPEN'
		) t
		ORDER BY score DESC, t.product_id ASC
		LIMIT 10
		""", nativeQuery = true)
	List<TrendingProductRow> findTrendingTop10IgnoringIndexes();

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
