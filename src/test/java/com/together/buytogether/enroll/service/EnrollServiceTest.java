package com.together.buytogether.enroll.service;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.member.domain.Address;
import com.together.buytogether.member.domain.Gender;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostStatus;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

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

    @Test
    @DisplayName("구매 참여 조건이 충족될 때 구매에 참여할 수 있다")
    void givenValidCondition_whenJoiningBuying_thenSuccess() {
        //given
        Member member = createMember();
        Post post = spy(createPostDto().toDomain(member));
        given(commonMemberService.getMember(1L)).willReturn(member);
        given(commonPostService.getPost(1L)).willReturn(post);
        given(enrollRepository.findByMemberIdAndPostId(1L, 1L)).willReturn(Optional.empty());

        //when
        enrollService.joinBuying(1L, 1L);

        //then
        then(enrollRepository).should().save(any(Enroll.class));
        verify(post, times(1)).increaseJoinCount();
    }


    @Test
    @DisplayName("이미 구매에 참여한 경우 참여할 수 없다")
    void givenAlreadyEnrolled_whenJoiningBuying_thenThrowsException() {
        //given
        Member member = createMember();
        Post post = createPostDto().toDomain(member);
        Enroll existingEnroll = Enroll.builder()
                .member(member)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
        given(commonMemberService.getMember(1L)).willReturn(member);
        given(commonPostService.getPost(1L)).willReturn(post);
        given(enrollRepository.findByMemberIdAndPostId(1L, 1L)).willReturn(Optional.of(existingEnroll));

        //when
        assertThrows(CustomException.class, () -> enrollService.joinBuying(1L, 1L));

        //then
        then(enrollRepository).should(never()).save(any(Enroll.class));

    }


    @Test
    @DisplayName("구매 참여를 취소할 수 있다")
    void givenExistingEnroll_whenCancelingBuying_thenSuccess() {
        //given
        Member member = createMember();
        Post post = spy(createPostDto().toDomain(member));
        Enroll existingEnroll = Enroll.builder()
                .member(member)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
        given(commonPostService.getPost(1L)).willReturn(post);
        given(enrollRepository.findByMemberIdAndPostId(1L, 1L)).willReturn(Optional.of(existingEnroll));

        //when
        enrollService.cancelBuying(1L, 1L);

        //then
        then(enrollRepository).should().delete(existingEnroll);
        verify(post, times(1)).decreaseJoinCount();
    }

    @Test
    @DisplayName("구매 참여한 적이 없을 경우 취소할 수 없다")
    void givenNonExistingEnroll_whenCancelingBuying_thenThrowsException() {
        //given
        Member member = createMember();
        Post post = spy(createPostDto().toDomain(member));
        given(commonPostService.getPost(1L)).willReturn(post);
        given(enrollRepository.findByMemberIdAndPostId(1L, 1L)).willReturn(Optional.empty());

        //when
        assertThrows(CustomException.class, () -> enrollService.cancelBuying(1L, 1L));

        //then
        then(enrollRepository).should(never()).save(any(Enroll.class));
    }

    private Member createMember() {
        return Member.builder()
                .name("name")
                .password("test")
                .phoneNumber("010-0000-0000")
                .gender(Gender.MALE)
                .loginId("test-id")
                .address(new Address("경기도", "고양시"))
                .build();
    }


    private RegisterPostDTO createPostDto() {
        return RegisterPostDTO.builder()
                .title("title")
                .content("content")
                .maxJoinCount(100L)
                .joinCount(1L)
                .status(PostStatus.OPEN)
                .expiredAt(LocalDateTime.now())
                .build();
    }
}
