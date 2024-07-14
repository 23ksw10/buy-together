package com.together.buytogether.post.controller;

import static com.together.buytogether.post.domain.PostResponseDTOFixture.*;
import static com.together.buytogether.post.domain.RegisterPostDTOFixture.*;
import static com.together.buytogether.post.domain.UpdatePostDTOFixture.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.post.dto.request.RegisterProductDTO;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import com.together.buytogether.post.dto.response.PostResponseDTO;
import com.together.buytogether.post.dto.response.RegisterProductResponseDTO;
import com.together.buytogether.post.service.PostService;
import com.together.buytogether.post.service.ProductService;

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
	@MockBean
	private ProductService productService;
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
				.session(mockSession))
			.andExpect(status().is2xxSuccessful())
			.andDo(log());

		then(postService).should()
			.registerPost((Long)mockSession.getAttribute(SessionConst.LOGIN_MEMBER), registerPostDTO);
	}

	@Test
	@DisplayName("게시글을 업데이트할 수 있다")
	void updatePost() throws Exception {
		UpdatePostDTO updatePostDTO = aUpdatePostDTO().build();

		mockMvc.perform(put("/posts/{postId}", postId)
				.content(objectMapper.writeValueAsString(updatePostDTO))
				.contentType(MediaType.APPLICATION_JSON)
				.session(mockSession))
			.andExpect(status().is2xxSuccessful())
			.andDo(log());
	}

	@Test
	@DisplayName("게시글을 조회할 수 있다")
	void getPost() throws Exception {
		PostResponseDTO postResponseDTO = aPostResponseDTO().build();
		ResponseDTO responseDTO = ResponseDTO.successResult(postResponseDTO);
		given(postService.getPost(postId)).willReturn(responseDTO);

		mockMvc.perform(get("/posts/{postId}", postId))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data.title").value("제목"))
			.andExpect(jsonPath("$.data.content").value("내용"))
			.andDo(log());

		then(postService).should().getPost(postId);
	}

	@Test
	@DisplayName("게시글을 삭제할 수 있다")
	void deletePost() throws Exception {

		mockMvc.perform(delete("/posts/{postId}", postId)
				.session(mockSession))
			.andExpect(status().is2xxSuccessful())
			.andDo(log());

		then(postService).should().deletePost(memberId, postId);
	}

	@Test
	@DisplayName("상품을 생성한다")
	void registerProduct() throws Exception {
		Long productId = 1L;
		Long price = 1000L;
		given(
			productService.registerProduct(any(Long.class), any(Long.class), any(RegisterProductDTO.class))).willReturn(
			ResponseDTO.successResult(RegisterProductResponseDTO.builder()
				.productId(productId)
				.price(price)
				.postId(postId)
				.build())
		);

		mockMvc.perform(post("/posts/{postId}", postId)
				.content(objectMapper.writeValueAsString(new RegisterProductDTO(price, 0L, 100L)))
				.contentType(MediaType.APPLICATION_JSON)
				.session(mockSession))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data.productId").value(productId))
			.andExpect(jsonPath("$.data.price").value(price))
			.andDo(log());
	}

}
