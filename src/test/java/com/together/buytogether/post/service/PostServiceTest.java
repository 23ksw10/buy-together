package com.together.buytogether.post.service;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.post.domain.PostStatus;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.together.buytogether.member.domain.MemberFixture.aMember;
import static com.together.buytogether.post.domain.PostFixture.aPost;
import static com.together.buytogether.post.domain.RegisterPostDTOFixture.aRegisterPostDTO;
import static com.together.buytogether.post.domain.UpdatePostDTOFixture.aUpdatePostDTO;
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

    Long memberId;

    Long postId;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        postId = 1L;
    }


    @Test
    @DisplayName("게시글을 등록할 수 있다")
    public void registerPostSuccess() {
        //given
        Member member = aMember().build();
        Post post = aPost().build();
        RegisterPostDTO registerPostDTO = aRegisterPostDTO().build();
        given(commonMemberService.getMember(any(Long.class))).willReturn(member);
        given(postRepository.save(any(Post.class))).willReturn(post);

        //when
        postService.registerPost(memberId, registerPostDTO);

        //then
        then(postRepository).should().save(any(Post.class));
        then(commonMemberService).should().getMember(any(Long.class));

    }

    @Test
    @DisplayName("존재하지 않는 회원은 게시글을 등록할 수 없다")
    public void registerPostFail() {
        //given

        given(commonMemberService.getMember(any(Long.class))).willThrow(CustomException.class);

        //when
        assertThrows(CustomException.class, () -> postService.registerPost(memberId, any(RegisterPostDTO.class)));

        //then
        then(postRepository).shouldHaveNoInteractions();

    }

    @Test
    @DisplayName("게시글을 수정한다")
    public void updatePostSuccess() {
        //given
        Member member = aMember().build();
        member.setId(1L);
        UpdatePostDTO updatePostDTO = aUpdatePostDTO().build();
        Post post = aPost().setMemberId(memberId).build();
        given(commonPostService.getPost(postId)).willReturn(post);

        //when
        postService.updatePost(memberId, postId, updatePostDTO);

        //then
        then(commonPostService).should().getPost(postId);
        assertEquals(post.getStatus(), PostStatus.CLOSED);

    }


    @Test
    @DisplayName("게시글 삭제 성공")
    public void deletePostSuccess() {
        //given
        Member member = aMember().build();
        member.setId(memberId);
        Post post = aPost().setMemberId(memberId).build();
        given(commonPostService.getPost(postId)).willReturn(post);

        //when
        postService.deletePost(memberId, postId);

        //then
        then(commonPostService.getPost(postId));
        then(postRepository).should().delete(post);
    }

    @Test
    @DisplayName("게시글 삭제 실패")
    public void deletePostFail() {
        //given
        Member member = aMember().build();
        member.setId(memberId);
        Post post = aPost().build();
        Long wrongMemberId = 99L;
        given(commonPostService.getPost(postId)).willReturn(post);

        //when
        assertThrows(CustomException.class, () -> postService.deletePost(wrongMemberId, postId));

        //then
        then(commonPostService).should().getPost(postId);
        then(postRepository).shouldHaveNoMoreInteractions();
    }



}
