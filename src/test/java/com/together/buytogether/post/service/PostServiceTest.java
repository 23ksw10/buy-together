package com.together.buytogether.post.service;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.member.domain.Address;
import com.together.buytogether.member.domain.Gender;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.post.domain.PostStatus;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    PostService postService;

    @Mock
    PostRepository postRepository;
    @Mock
    CommonMemberService commonMemberService;
    @Mock
    CommonPostService commonPostService;


    @Test
    @DisplayName("게시글을 등록할 수 있다")
    public void registerPost_success() {
        //given
        Member member = createMember();
        Post post = createPostDto().toDomain(member);
        given(commonMemberService.getMember(any(Long.class))).willReturn(member);
        given(postRepository.save(any(Post.class))).willReturn(post);

        //when
        postService.registerPost(1L, createPostDto());

        //then
        then(postRepository).should().save(any(Post.class));
        then(commonMemberService).should().getMember(any(Long.class));

    }

    @Test
    @DisplayName("존재하지 않는 회원은 게시글을 등록할 수 없다")
    public void registerPost_fail() {
        //given

        given(commonMemberService.getMember(any(Long.class))).willThrow(CustomException.class);

        //when
        assertThrows(CustomException.class, () -> postService.registerPost(1L, any(RegisterPostDTO.class)));

        //then
        then(postRepository).shouldHaveNoInteractions();

    }

    @Test
    @DisplayName("게시글을 수정한다")
    public void updatePost_success() {
        //given
        Member member = createMember();
        member.setId(1L);
        UpdatePostDTO updatePostDTO = createUpdatePostDto();
        Post post = createPostDto().toDomain(member);
        given(commonPostService.getPost(1L)).willReturn(post);

        //when
        postService.updatePost(1L, 1L, updatePostDTO);

        //then
        then(commonPostService).should().getPost(1L);
        assertEquals(post.getStatus(), PostStatus.CLOSED);

    }


    @Test
    @DisplayName("게시글 삭제 성공")
    public void deletePost_success() {
        //given
        Member member = createMember();
        member.setId(1L);
        Post post = createPostDto().toDomain(member);
        given(commonPostService.getPost(1L)).willReturn(post);

        //when
        postService.deletePost(1L, 1L);

        //then
        then(commonPostService.getPost(1L));
        then(postRepository).should().delete(post);
    }

    @Test
    @DisplayName("게시글 삭제 실패")
    public void deletePost_fail() {
        //given
        Member member = createMember();
        member.setId(1L);
        Post post = createPostDto().toDomain(member);
        given(commonPostService.getPost(1L)).willReturn(post);

        //when
        assertThrows(CustomException.class, () -> postService.deletePost(99L, 1L));

        //then
        then(commonPostService).should().getPost(1L);
        then(postRepository).shouldHaveNoMoreInteractions();
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
                .joinCount(0L)
                .status(PostStatus.OPEN)
                .expiredAt(LocalDateTime.now())
                .build();
    }

    private UpdatePostDTO createUpdatePostDto() {
        return UpdatePostDTO.builder()
                .title("title")
                .content("content")
                .maxJoinCount(100L)
                .status(PostStatus.CLOSED)
                .expiredAt(LocalDateTime.now())
                .build();
    }
}
