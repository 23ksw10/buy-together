package com.together.buytogether.enroll.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;

public interface EnrollRepository extends JpaRepository<Enroll, Long>, CustomizeEnrollRepository {
	@Query("SELECT e FROM Enroll e JOIN FETCH e.member m JOIN FETCH e.product p WHERE e.enrollId =:enrollId")
	Optional<Enroll> findByEnrollId(@Param("enrollId") Long enrollId);

	@Query("SELECT e FROM Enroll e JOIN FETCH e.member m JOIN FETCH e.product p WHERE m.memberId = :memberId AND p.productId = :productId")
	Optional<Enroll> findByMemberIdAndProductId(@Param("memberId") Long memberId, @Param("productId") Long productId);

	default Enroll getEnroll(Long enrollId) {
		return findByEnrollId(enrollId)
			.orElseThrow(() -> new CustomException(ErrorCode.ENROLL_NOT_FOUND));
	}
}
