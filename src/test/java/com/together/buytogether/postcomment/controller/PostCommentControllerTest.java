package com.together.buytogether.postcomment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.domain.PostStatus;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.postcomment.dto.request.RegisterCommentDTO;
import com.together.buytogether.postcomment.dto.request.UpdateCommentDTO;
import com.together.buytogether.postcomment.service.PostCommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("PostComment Controller 테스트")
@WebMvcTest(controllers = PostCommentController.class)
public class PostCommentControllerTest {
    Long postId;
    Long commentId;
    Long memberId;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PostCommentService postCommentService;

    @BeforeEach
    void setUp() {
        postId = 1L;
        commentId = 1L;
        memberId = 1L;
    }

    @Test
    @DisplayName("댓글을 등록할 수 있다")
    void givenValidData_whenRegisteringPostComment_then200() throws Exception {
        RegisterCommentDTO registerCommentDTO = createRegisterCommentDTO();

        mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .content(objectMapper.writeValueAsString(registerCommentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, memberId))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postCommentService).should().registerComment(memberId, postId, registerCommentDTO);

    }

    @Test
    @DisplayName("댓글을 삭제할 수 있다")
    void givenValidData_whenDeletingPostComment_then200() throws Exception {

        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postCommentService).should().deleteComment(memberId, commentId);

    }

    @Test
    @DisplayName("댓글을 업데이트 수 있다")
    void givenValidData_whenUpdatingPostComment_then200() throws Exception {
        UpdateCommentDTO updateCommentDTO = UpdateCommentDTO.builder()
                .content("newContent")
                .build();

        mockMvc.perform(put("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .content(objectMapper.writeValueAsString(updateCommentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postCommentService).should().updateComment(memberId, commentId, updateCommentDTO);

    }

    @Test
    @DisplayName("특정 댓글을 가져올 수 있다")
    void givenValidData_whenGetPostComment_then200() throws Exception {


        mockMvc.perform(get("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postCommentService).should().getPostComment(postId);

    }

    @Test
    @DisplayName("게시글에 있는 모든 댓글을 가져올 수 있다")
    void givenValidData_whenGetAllPostComment_then200() throws Exception {


        mockMvc.perform(get("/posts/{postId}/comments", postId)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postCommentService).should().getPostComments(postId);

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
