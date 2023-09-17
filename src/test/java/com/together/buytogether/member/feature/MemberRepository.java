package com.together.buytogether.member.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MemberRepository {
    private final Map<Long, Member> members = new HashMap<>();
    private Long memberId = 0L;

    public void save(Member member) {
        member.assignId(memberId++);
        members.put(member.getId(), member);
    }

    public List<Member> findAll() {
        return new ArrayList<>(members.values());
    }
}
