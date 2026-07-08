package com.together.buytogether.product.controller;

import static com.together.buytogether.product.domain.ProductResponseDTOFixture.*;
import static com.together.buytogether.product.domain.RegisterProductDTOFixture.*;
import static com.together.buytogether.product.domain.UpdateProductDTOFixture.*;
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
import com.together.buytogether.product.dto.request.RegisterProductDTO;
import com.together.buytogether.product.dto.request.UpdateProductDTO;
import com.together.buytogether.product.dto.response.ProductResponseDTO;
import com.together.buytogether.product.service.ProductService;

@DisplayName("Product Controller 테스트")
@WebMvcTest(controllers = ProductController.class)
public class ProductControllerTest {

	Long productId;
	Long memberId;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private ProductService productService;
	private MockHttpSession mockSession;

	@BeforeEach
	void setUp() {
		mockSession = new MockHttpSession();
		mockSession.setAttribute(SessionConst.LOGIN_MEMBER, 1L);
		productId = 1L;
		memberId = 1L;
	}

	@Test
	@DisplayName("상품을 등록할 수 있다")
	void registerProduct() throws Exception {
		RegisterProductDTO registerProductDTO = aRegisterProductDTO().build();

		mockMvc.perform(post("/products")
				.content(objectMapper.writeValueAsString(registerProductDTO))
				.contentType(MediaType.APPLICATION_JSON)
				.session(mockSession))
			.andExpect(status().is2xxSuccessful())
			.andDo(log());

		then(productService).should()
			.registerProduct((Long)mockSession.getAttribute(SessionConst.LOGIN_MEMBER), registerProductDTO);
	}

	@Test
	@DisplayName("상품을 업데이트할 수 있다")
	void updateProduct() throws Exception {
		UpdateProductDTO updateProductDTO = aUpdateProductDTO().build();

		mockMvc.perform(put("/products/{productId}", productId)
				.content(objectMapper.writeValueAsString(updateProductDTO))
				.contentType(MediaType.APPLICATION_JSON)
				.session(mockSession))
			.andExpect(status().is2xxSuccessful())
			.andDo(log());

		then(productService).should().updateProduct(memberId, productId, updateProductDTO);
	}

	@Test
	@DisplayName("상품을 조회할 수 있다")
	void getProduct() throws Exception {
		ProductResponseDTO productResponseDTO = aProductResponseDTO().build();
		ResponseDTO<ProductResponseDTO> responseDTO = ResponseDTO.successResult(productResponseDTO);
		given(productService.getProduct(productId)).willReturn(responseDTO);

		mockMvc.perform(get("/products/{productId}", productId))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data.title").value("제목"))
			.andExpect(jsonPath("$.data.content").value("내용"))
			.andDo(log());

		then(productService).should().getProduct(productId);
	}

	@Test
	@DisplayName("상품을 삭제할 수 있다")
	void deleteProduct() throws Exception {
		mockMvc.perform(delete("/products/{productId}", productId)
				.session(mockSession))
			.andExpect(status().is2xxSuccessful())
			.andDo(log());

		then(productService).should().deleteProduct(memberId, productId);
	}
}
