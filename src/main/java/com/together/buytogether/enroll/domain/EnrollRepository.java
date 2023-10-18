package com.together.buytogether.enroll.domain;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EnrollRepository {
    private final Map<Long, Enroll> enrollMap = new HashMap<>();
    Long enrollId = 1L;

    public Optional<Enroll> findByMemberId(Long memberId) {
        return Optional.ofNullable(enrollMap.get(memberId));
    }

    public List<Enroll> findAll() {
        return new ArrayList<>(enrollMap.values());
    }

    public void save(Enroll enroll) {
        enroll.assignId(enrollId);
        enrollId++;
        enrollMap.put(enrollId, enroll);
    }
}
