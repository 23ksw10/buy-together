package com.together.buytogether.enroll.domain;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "enroll")
@NoArgsConstructor
@Comment("구매 참여")
public class Enroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enroll_id")
    private Long enrollId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    @CreatedDate
    private LocalDateTime createdAt;

    public Enroll(Member member, Post post, LocalDateTime createdAt) {
        this.member = member;
        this.post = post;
        this.createdAt = createdAt;
    }
}
