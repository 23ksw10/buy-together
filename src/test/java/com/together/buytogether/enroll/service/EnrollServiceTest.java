package com.together.buytogether.enroll.service;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.together.buytogether.member.domain.MemberFixture.aMember;
import static com.together.buytogether.post.domain.PostFixture.aPost;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EnrollServiceTest {
    @Mock
    CommonMemberService commonMemberService;

    @Mock
    CommonPostService commonPostService;

    @Mock
    EnrollRepository enrollRepository;

    @InjectMocks
    EnrollService enrollService;

    Long memberId;
    Long postId;
    private Member member;
    private Post post;

    @BeforeEach
    void setUp() {
        member = aMember().build();
        post = spy(aPost().member(member).build());
        memberId = 1L;
        postId = 1L;
    }

    @Test
    @DisplayName("구매 참여 조건이 충족될 때 구매에 참여할 수 있다")
    void joinBuyingSuccess() {
        //given
        given(commonMemberService.getMember(memberId)).willReturn(member);
        given(commonPostService.getPost(postId)).willReturn(post);
        given(enrollRepository.findByMemberIdAndPostId(memberId, postId)).willReturn(Optional.empty());

        //when
        enrollService.joinBuying(memberId, postId);

        //then
        then(enrollRepository).should().save(any(Enroll.class));
        verify(post, times(1)).increaseJoinCount();
    }


    @Test
    @DisplayName("이미 구매에 참여한 경우 참여할 수 없다")
    void alreadyEnrolledJoinBuyingFail() {
        //given
        Enroll existingEnroll = Enroll.builder()
                .member(member)
                .post(post)
                .build();
        given(commonMemberService.getMember(memberId)).willReturn(member);
        given(commonPostService.getPost(postId)).willReturn(post);
        given(enrollRepository.findByMemberIdAndPostId(memberId, postId)).willReturn(Optional.of(existingEnroll));

        //when
        assertThrows(CustomException.class, () -> enrollService.joinBuying(memberId, postId));

        //then
        then(enrollRepository).should(never()).save(any(Enroll.class));

    }


    @Test
    @DisplayName("구매 참여를 취소할 수 있다")
    void cancelBuyingSuccess() {
        //given
        Enroll existingEnroll = Enroll.builder()
                .member(member)
                .post(post)
                .build();
        given(commonPostService.getPost(postId)).willReturn(post);
        given(enrollRepository.getEnroll(memberId, postId)).willReturn(existingEnroll);

        //when
        enrollService.cancelBuying(memberId, postId);

        //then
        then(enrollRepository).should().delete(existingEnroll);
        verify(post, times(1)).decreaseJoinCount();
    }

    @Test
    @DisplayName("구매 참여한 적이 없을 경우 취소할 수 없다")
    void nonExistingEnrollCancelBuyingFail() {
        //given
        given(commonPostService.getPost(postId)).willReturn(post);
        given(enrollRepository.getEnroll(memberId, postId)).willThrow(CustomException.class);

        //when
        assertThrows(CustomException.class, () -> enrollService.cancelBuying(1L, 1L));

        //then
        then(enrollRepository).should(never()).save(any(Enroll.class));
    }

}
