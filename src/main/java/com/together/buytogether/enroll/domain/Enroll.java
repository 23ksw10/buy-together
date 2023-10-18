package com.together.buytogether.enroll.domain;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;

public class Enroll {
    private final Member member;
    private final Post post;
    private Long enrollId;

    public Enroll(Member member, Post post) {
        this.member = member;
        this.post = post;
    }

    public void assignId(Long enrollId) {
        this.enrollId = enrollId;
    }
}
