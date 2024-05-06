package com.together.buytogether.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.domain.PostStatus;
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

import java.time.LocalDateTime;

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
    void givenValidData_whenRegisteringPost_then200() throws Exception {
        RegisterPostDTO registerPostDTO = createRegisterPostDto();

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
    void givenValidData_whenUpdatingPost_then200() throws Exception {
        UpdatePostDTO updatePostDTO = createUpdatePostDto();

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
    void whenRetrievingPost_thenSuccessful() throws Exception {
        PostResponseDTO postResponseDTO = createPostResponseDTO();
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
    void givenPostIdAndMemberId_whenDeletingPost_thenSuccessfullyDeleted() throws Exception {


        mockMvc.perform(delete("/posts/{postId}", postId)
                        .sessionAttr("memberId", 1L)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())

                .andDo(log());

        then(postService).should().deletePost(memberId, postId);
    }

    private PostResponseDTO createPostResponseDTO() {
        return PostResponseDTO.builder()
                .memberName("작성자 이름")
                .postId(1L)
                .title("제목")
                .content("내용")
                .expiredAt("2023-06-07T10:00:00")
                .build();
    }

    private RegisterPostDTO createRegisterPostDto() {
        return RegisterPostDTO.builder()
                .title("title")
                .content("content")
                .status(PostStatus.OPEN)
                .expiredAt(LocalDateTime.now())
                .maxJoinCount(100L)
                .joinCount(1L)
                .build();
    }

    private UpdatePostDTO createUpdatePostDto() {
        return UpdatePostDTO.builder()
                .title("newTitle")
                .content("newContent")
                .status(PostStatus.CLOSED)
                .expiredAt(LocalDateTime.now())
                .maxJoinCount(100L)
                .build();
    }
}
