package com.together.buytogether.enroll.domain;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberFixture;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostFixture;

public class EnrollFixture {
    private Member member = MemberFixture.aMember().build();
    private Post post = PostFixture.aPost().build();

    public static EnrollFixture aEnroll() {
        return new EnrollFixture();
    }

    public EnrollFixture member(Member member) {
        this.member = member;
        return this;
    }

    public EnrollFixture post(Post post) {
        this.post = post;
        return this;
    }

    public Enroll build() {
        return new Enroll(member, post);
    }
}
