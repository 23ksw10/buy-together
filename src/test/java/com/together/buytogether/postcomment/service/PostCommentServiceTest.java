package com.together.buytogether.postcomment.service;

import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.member.domain.Address;
import com.together.buytogether.member.domain.Gender;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostStatus;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.postcomment.domain.PostComment;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import com.together.buytogether.postcomment.dto.request.RegisterCommentDTO;
import com.together.buytogether.postcomment.dto.request.UpdateCommentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class PostCommentServiceTest {
    @Mock
    CommonMemberService commonMemberService;

    @Mock
    CommonPostService commonPostService;

    @Mock
    PostCommentRepository postCommentRepository;

    @InjectMocks
    PostCommentService postCommentService;

    Long postId;
    Long memberId;
    Long commentId;
    Member member;
    Post post;
    RegisterCommentDTO registerCommentDTO;
    PostComment postComment;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        postId = 1L;
        commentId = 1L;
        member = createMember();
        member.setId(memberId);
        post = createPostDto().toDomain(member);
        registerCommentDTO = createRegisterCommentDTO();
        postComment = registerCommentDTO.toDomain(member, post);
    }

    @Test
    @DisplayName("게시글에 댓글을 등록할 수 있다")
    void givenValidData_whenRegisteringComment_thenSuccessful() {
        //given

        given(commonMemberService.getMember(memberId)).willReturn(member);
        given(commonPostService.getPost(postId)).willReturn(post);
        given(postCommentRepository.save(any(PostComment.class))).willReturn(postComment);

        //when
        postCommentService.registerComment(memberId, postId, registerCommentDTO);

        //then
        then(postCommentRepository).should().save(refEq(postComment));
    }

    @Test
    @DisplayName("게시글에 댓글을 수정할 수 있다")
    void givenValidData_whenUpdatingComment_thenSuccessful() {
        //given
        postComment.setCommentId(commentId);
        UpdateCommentDTO updateCommentDTO = UpdateCommentDTO.builder()
                .content("update-content")
                .build();

        given(postCommentRepository.getByCommentId(commentId)).willReturn(postComment);

        //when
        postCommentService.updateComment(memberId, commentId, updateCommentDTO);


        //then
        assertEquals(postComment.getContent(), "update-content");
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    public void givenValidData_whenDeletingComment_thenSuccessful() {
        given(postCommentRepository.getByCommentId(commentId))
                .willReturn(postComment);

        postCommentService.deleteComment(commentId, memberId);

        then(postCommentRepository).should().delete(refEq(postComment));
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

    private RegisterCommentDTO createRegisterCommentDTO() {
        return RegisterCommentDTO.builder()
                .content("content")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
    }
}
