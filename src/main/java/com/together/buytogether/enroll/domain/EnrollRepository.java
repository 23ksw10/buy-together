package com.together.buytogether.enroll.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EnrollRepository extends JpaRepository<Enroll, Long> {
    @Query("select e from Enroll e join fetch e.member m join fetch e.post p where m.memberId = :memberId and p.postId = :postId")
    Optional<Enroll> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    default Enroll getEnroll(Long memberId, Long postId) {
        return findByMemberIdAndPostId(memberId, postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 구매 정보가 없습니다."));
    }
}
