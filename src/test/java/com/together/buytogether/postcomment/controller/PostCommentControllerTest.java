package com.together.buytogether.postcomment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.postcomment.dto.request.CommentDTO;
import com.together.buytogether.postcomment.service.PostCommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("PostComment Controller 테스트")
@WebMvcTest(controllers = PostCommentController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
    void registerCommentSuccess() throws Exception {
        CommentDTO commentDTO = new CommentDTO("content");

        mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .content(objectMapper.writeValueAsString(commentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, memberId))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postCommentService).should().registerComment(memberId, postId, commentDTO);

    }

    @Test
    @DisplayName("댓글을 삭제할 수 있다")
    void deletePostCommentSuccess() throws Exception {

        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postCommentService).should().deleteComment(memberId, commentId);

    }

    @Test
    @DisplayName("댓글을 업데이트 수 있다")
    void updateCommentSuccess() throws Exception {
        CommentDTO commentDTO = new CommentDTO("content");

        mockMvc.perform(put("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .content(objectMapper.writeValueAsString(commentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postCommentService).should().updateComment(memberId, commentId, commentDTO);

    }

    @Test
    @DisplayName("특정 댓글을 가져올 수 있다")
    void getPostCommentSuccess() throws Exception {


        mockMvc.perform(get("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postCommentService).should().getPostComment(postId);

    }

    @Test
    @DisplayName("게시글에 있는 모든 댓글을 가져올 수 있다")
    void getAllPostCommentsSuccess() throws Exception {


        mockMvc.perform(get("/posts/{postId}/comments", postId)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postCommentService).should().getPostComments(postId);

    }
}
