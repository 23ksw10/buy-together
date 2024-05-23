package com.together.buytogether.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import com.together.buytogether.post.dto.response.PostResponseDTO;
import com.together.buytogether.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static com.together.buytogether.post.domain.PostResponseDTOFixture.aPostResponseDTO;
import static com.together.buytogether.post.domain.RegisterPostDTOFixture.aRegisterPostDTO;
import static com.together.buytogether.post.domain.UpdatePostDTOFixture.aUpdatePostDTO;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Post Controller 테스트")
@WebMvcTest(controllers = PostController.class)
public class PostControllerTest {

    Long postId;
    Long memberId;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PostService postService;
    private MockHttpSession mockSession;

    @BeforeEach
    void setUp() {
        mockSession = new MockHttpSession();
        mockSession.setAttribute(SessionConst.LOGIN_MEMBER, 1L);
        postId = 1L;
        memberId = 1L;
    }

    @Test
    @DisplayName("게시글을 등록할 수 있다")
    void registerPost() throws Exception {
        RegisterPostDTO registerPostDTO = aRegisterPostDTO().build();

        mockMvc.perform(post("/posts")
                        .content(objectMapper.writeValueAsString(registerPostDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("memberId", 1L)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postService).should().registerPost((Long) mockSession.getAttribute(SessionConst.LOGIN_MEMBER), registerPostDTO);
    }

    @Test
    @DisplayName("게시글을 업데이트할 수 있다")
    void updatePost() throws Exception {
        UpdatePostDTO updatePostDTO = aUpdatePostDTO().build();

        mockMvc.perform(put("/posts/{postId}", postId)
                        .content(objectMapper.writeValueAsString(updatePostDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("memberId", 1L)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());
    }

    @Test
    @DisplayName("게시글을 조회할 수 있다")
    void getPost() throws Exception {
        PostResponseDTO postResponseDTO = aPostResponseDTO().build();
        given(postService.getPost(postId)).willReturn(postResponseDTO);

        mockMvc.perform(get("/posts/{postId}", postId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andDo(log());

        then(postService).should().getPost(postId);
    }

    @Test
    @DisplayName("게시글을 삭제할 수 있다")
    void deletePost() throws Exception {


        mockMvc.perform(delete("/posts/{postId}", postId)
                        .sessionAttr("memberId", 1L)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())

                .andDo(log());

        then(postService).should().deletePost(memberId, postId);
    }


}
