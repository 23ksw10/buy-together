package com.together.buytogether.product.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.together.buytogether.product.dto.response.TrendingProductResponseDTO;
import com.together.buytogether.product.service.TrendingProductService;

@DisplayName("TrendingProduct Controller 테스트")
@WebMvcTest(controllers = TrendingProductController.class)
class TrendingProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TrendingProductService trendingProductService;

	private List<TrendingProductResponseDTO> response;

	@BeforeEach
	void setUp() {
		response = List.of(new TrendingProductResponseDTO(
			1L,
			"인기 상품",
			10_000L,
			3L,
			2L,
			8L
		));
	}

	@Test
	@DisplayName("v1 인기 상품을 조회할 수 있다")
	void getTrendingV1() throws Exception {
		given(trendingProductService.getTrendingV1()).willReturn(response);

		assertTrendingResponse(mockMvc.perform(get("/group-buys/trending/v1")));

		then(trendingProductService).should().getTrendingV1();
	}

	@Test
	@DisplayName("v2 인기 상품을 조회할 수 있다")
	void getTrendingV2() throws Exception {
		given(trendingProductService.getTrendingV2()).willReturn(response);

		assertTrendingResponse(mockMvc.perform(get("/group-buys/trending/v2")));

		then(trendingProductService).should().getTrendingV2();
	}

	@Test
	@DisplayName("v3 인기 상품을 조회할 수 있다")
	void getTrendingV3() throws Exception {
		given(trendingProductService.getTrendingV3()).willReturn(response);

		assertTrendingResponse(mockMvc.perform(get("/group-buys/trending/v3")));

		then(trendingProductService).should().getTrendingV3();
	}

	@Test
	@DisplayName("v4 인기 상품을 조회할 수 있다")
	void getTrendingV4() throws Exception {
		given(trendingProductService.getTrendingV4()).willReturn(response);

		assertTrendingResponse(mockMvc.perform(get("/group-buys/trending/v4")));

		then(trendingProductService).should().getTrendingV4();
	}

	@Test
	@DisplayName("v5 인기 상품을 조회할 수 있다")
	void getTrendingV5() throws Exception {
		given(trendingProductService.getTrendingV5()).willReturn(response);

		assertTrendingResponse(mockMvc.perform(get("/group-buys/trending/v5")));

		then(trendingProductService).should().getTrendingV5();
	}

	@Test
	@DisplayName("기본 경로는 v5 인기 상품을 조회한다")
	void getTrendingDefault() throws Exception {
		given(trendingProductService.getTrendingV5()).willReturn(response);

		assertTrendingResponse(mockMvc.perform(get("/group-buys/trending")));

		then(trendingProductService).should().getTrendingV5();
	}

	private void assertTrendingResponse(ResultActions resultActions) throws Exception {
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("SUCCESS"))
			.andExpect(jsonPath("$.message").value("Request Success"))
			.andExpect(jsonPath("$.data[0].productId").value(1))
			.andExpect(jsonPath("$.data[0].title").value("인기 상품"))
			.andExpect(jsonPath("$.data[0].price").value(10_000))
			.andExpect(jsonPath("$.data[0].enrollCount").value(3))
			.andExpect(jsonPath("$.data[0].likeCount").value(2))
			.andExpect(jsonPath("$.data[0].score").value(8));
	}
}
