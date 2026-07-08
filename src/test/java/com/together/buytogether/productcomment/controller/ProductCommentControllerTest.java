package com.together.buytogether.productcomment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.productcomment.dto.request.CommentDTO;
import com.together.buytogether.productcomment.service.ProductCommentService;
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

@DisplayName("ProductComment Controller 테스트")
@WebMvcTest(controllers = ProductCommentController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductCommentControllerTest {
    Long productId;
    Long commentId;
    Long memberId;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProductCommentService productCommentService;

    @BeforeEach
    void setUp() {
        productId = 1L;
        commentId = 1L;
        memberId = 1L;
    }

    @Test
    @DisplayName("댓글을 등록할 수 있다")
    void registerCommentSuccess() throws Exception {
        CommentDTO commentDTO = new CommentDTO("content");

        mockMvc.perform(post("/products/{productId}/comments", productId)
                        .content(objectMapper.writeValueAsString(commentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, memberId))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(productCommentService).should().registerComment(memberId, productId, commentDTO);

    }

    @Test
    @DisplayName("댓글을 삭제할 수 있다")
    void deleteProductCommentSuccess() throws Exception {

        mockMvc.perform(delete("/products/{productId}/comments/{commentId}", productId, commentId)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(productCommentService).should().deleteComment(memberId, commentId);

    }

    @Test
    @DisplayName("댓글을 업데이트 수 있다")
    void updateCommentSuccess() throws Exception {
        CommentDTO commentDTO = new CommentDTO("content");

        mockMvc.perform(put("/products/{productId}/comments/{commentId}", productId, commentId)
                        .content(objectMapper.writeValueAsString(commentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(productCommentService).should().updateComment(memberId, commentId, commentDTO);

    }

    @Test
    @DisplayName("특정 댓글을 가져올 수 있다")
    void getProductCommentSuccess() throws Exception {


        mockMvc.perform(get("/products/{productId}/comments/{commentId}", productId, commentId)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(productCommentService).should().getProductComment(productId);

    }

    @Test
    @DisplayName("상품에 있는 모든 댓글을 가져올 수 있다")
    void getAllProductCommentsSuccess() throws Exception {


        mockMvc.perform(get("/products/{productId}/comments", productId)
                        .sessionAttr(SessionConst.LOGIN_MEMBER, 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(productCommentService).should().getProductComments(productId);

    }
}
