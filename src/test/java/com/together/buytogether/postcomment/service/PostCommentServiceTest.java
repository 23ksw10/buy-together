package com.together.buytogether.postcomment.service;

import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.postcomment.domain.PostComment;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import com.together.buytogether.postcomment.dto.request.CommentDTO;
import com.together.buytogether.postcomment.dto.response.RegisterCommentResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.together.buytogether.member.domain.MemberFixture.aMember;
import static com.together.buytogether.post.domain.PostFixture.aPost;
import static com.together.buytogether.postcomment.domain.PostCommentFixture.aPostComment;
import static org.assertj.core.api.Assertions.assertThat;
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
    CommentDTO commentDTO;
    PostComment postComment;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        postId = 1L;
        commentId = 1L;
        member = aMember().build();
        member.setId(memberId);
        post = aPost().member(member).build();
        post.setPostId(postId);
        commentDTO = new CommentDTO("content");
        postComment = aPostComment().member(member).post(post).build();
        postComment.setCommentId(commentId);
        postComment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("게시글에 댓글을 등록할 수 있다")
    void registerCommentSuccess() {
        //given

        given(commonMemberService.getMember(memberId)).willReturn(member);
        given(commonPostService.getPost(postId)).willReturn(post);
        given(postCommentRepository.save(any(PostComment.class))).willReturn(postComment);

        //when
        ResponseDTO<RegisterCommentResponseDTO> responseDTO = postCommentService.registerComment(memberId, postId, commentDTO);

        //then
        then(postCommentRepository).should().save(any(PostComment.class));
        assertThat(responseDTO.getData()).isNotNull();
    }

    @Test
    @DisplayName("게시글에 댓글을 수정할 수 있다")
    void updateCommentSuccess() {
        //given
        postComment.setCommentId(commentId);
        CommentDTO commentDTO = new CommentDTO("update-content");

        given(postCommentRepository.getByCommentId(commentId)).willReturn(postComment);

        //when
        postCommentService.updateComment(memberId, commentId, commentDTO);


        //then
        assertEquals(postComment.getContent(), "update-content");
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    public void deleteCommentSuccess() {
        given(postCommentRepository.getByCommentId(commentId))
                .willReturn(postComment);

        postCommentService.deleteComment(commentId, memberId);

        then(postCommentRepository).should().delete(refEq(postComment));
    }

}
