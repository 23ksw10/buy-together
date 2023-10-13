package com.together.buytogether.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);

    default Member getByMemberId(Long memberId) {
        return findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다")
        );
    }
}
