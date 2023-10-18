package com.together.buytogether.enroll.feature;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberFixture;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostFixture;
import com.together.buytogether.post.domain.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class JoinBuyingTest {
    JoinBuying joinBuying;
    PostRepository postRepository;
    EnrollRepository enrollRepository;
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        postRepository = Mockito.mock(PostRepository.class);
        enrollRepository = new EnrollRepository();
        memberRepository = Mockito.mock(MemberRepository.class);
        joinBuying = new JoinBuying(postRepository, enrollRepository, memberRepository);
    }

    @Test
    @DisplayName("구매 참여")
    void joinBuying() {
        PostFixture postFixture = PostFixture.aPost();
        Mockito.when(postRepository.getByPostId(1L)).thenReturn(postFixture.build());
        MemberFixture memberFixture = MemberFixture.aMember();
        Mockito.when(memberRepository.getByMemberId(1L)).thenReturn(memberFixture.build());
        Long memberId = 1L;
        Long postId = 1L;
        joinBuying.requset(memberId, postId);
        assertThat(enrollRepository.findAll().size()).isEqualTo(1);
    }

    private class JoinBuying {
        PostRepository postRepository;
        EnrollRepository enrollRepository;
        MemberRepository memberRepository;

        public JoinBuying(
                PostRepository postRepository,
                EnrollRepository enrollRepository,
                MemberRepository memberRepository) {
            this.postRepository = postRepository;
            this.enrollRepository = enrollRepository;
            this.memberRepository = memberRepository;
        }

        public void requset(Long memberId, Long postId) {
            Post post = postRepository.getByPostId(postId);
            Member member = memberRepository.getByMemberId(memberId);
            enrollRepository.findByMemberId(memberId).ifPresent(enroll -> {
                throw new IllegalArgumentException("이미 참여한 구매글입니다.");
            });
            Enroll enroll = new Enroll(member, post);
            post.increaseJoinCount();
            enrollRepository.save(enroll);
        }
    }

    private class EnrollRepository {
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

    private class Enroll {
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
}
