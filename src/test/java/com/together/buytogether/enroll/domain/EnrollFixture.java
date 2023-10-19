package com.together.buytogether.enroll.domain;

import com.together.buytogether.member.domain.MemberFixture;
import com.together.buytogether.post.domain.PostFixture;

import java.time.LocalDateTime;

public class EnrollFixture {
    private MemberFixture memberFixture = MemberFixture.aMember();
    private PostFixture postFixture = PostFixture.aPost();

    private LocalDateTime createdAt = LocalDateTime.now();

    public static EnrollFixture aEnroll() {
        return new EnrollFixture();
    }

    public EnrollFixture memberFixture(MemberFixture memberFixture) {
        this.memberFixture = memberFixture;
        return this;
    }

    public EnrollFixture postFixture(PostFixture postFixture) {
        this.postFixture = postFixture;
        return this;
    }

    public EnrollFixture createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Enroll build() {
        return new Enroll(memberFixture.build(), postFixture.build(), createdAt);
    }
}
