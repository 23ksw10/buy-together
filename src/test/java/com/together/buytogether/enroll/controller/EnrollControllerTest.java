package com.together.buytogether.enroll.controller;

import static com.together.buytogether.enroll.domain.JoinEnrollDTOFixture.*;
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
import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.enroll.dto.request.JoinEnrollDTO;
import com.together.buytogether.enroll.service.EnrollFacade;
import com.together.buytogether.member.domain.SessionConst;

@DisplayName("Enroll Controller 테스트")
@WebMvcTest(controllers = EnrollController.class)
public class EnrollControllerTest {

	@MockBean
	private EnrollFacade enrollFacade;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	Long memberId;
	MockHttpSession mockSession;

	@BeforeEach
	void setUp() {
		memberId = 1L;
		mockSession = new MockHttpSession();
		mockSession.setAttribute(SessionConst.LOGIN_MEMBER, 1L);
	}

	@Test
	@DisplayName("구매에 참여할 수 있다")
	void joinBuying() throws Exception {
		JoinEnrollDTO joinEnrollDTO = aJoinEnrollDTOFixture().build();
		mockMvc.perform(post("/enrolls")
				.content(objectMapper.writeValueAsString(joinEnrollDTO))
				.contentType(MediaType.APPLICATION_JSON)
				.session(mockSession))
			.andExpect(status().is2xxSuccessful())
			.andDo(log());

	}

	@Test
	@DisplayName("요청이 몰려 락을 얻지 못하면 503과 재시도 안내 메시지로 응답한다")
	void joinBuyingReturns503WhenLockAcquisitionFails() throws Exception {
		JoinEnrollDTO joinEnrollDTO = aJoinEnrollDTOFixture().build();
		given(enrollFacade.joinBuying(any(Long.class), any(JoinEnrollDTO.class)))
			.willThrow(new CustomException(ErrorCode.LOCK_ACQUISITION_FAILED));

		mockMvc.perform(post("/enrolls")
				.content(objectMapper.writeValueAsString(joinEnrollDTO))
				.contentType(MediaType.APPLICATION_JSON)
				.session(mockSession))
			.andExpect(status().isServiceUnavailable())
			.andExpect(jsonPath("$.message").value(ErrorCode.LOCK_ACQUISITION_FAILED.getMessage()))
			.andDo(log());
	}

	@Test
	@DisplayName("로그인 되어있지 않다면 구매에 참여할 수 없다")
	void cannotJoinBuyingWhenNotLoggedIn() throws Exception {
		JoinEnrollDTO joinEnrollDTO = aJoinEnrollDTOFixture().build();
		mockMvc.perform(post("/enrolls")
				.content(objectMapper.writeValueAsString(joinEnrollDTO))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized())
			.andDo(log());

	}

	@Test
	@DisplayName("구매를 취소할 수 있다")
	void cancelBuying() throws Exception {
		Long enrollId = 1L;
		given(enrollFacade.cancelBuying(any(Long.class), any(Long.class))).willReturn(
			ResponseDTO.successResult("구매가 취소되셨습니다"));
		mockMvc.perform(delete("/enrolls/{enrollId}", enrollId)
				.session(mockSession))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.status").value("SUCCESS"))
			.andExpect(jsonPath("$.data").value("구매가 취소되셨습니다"))
			.andDo(log());

	}

	@Test
	@DisplayName("로그인 되어있지 않다면 구매취소를 할 수 없다")
	void cannotCancelBuyingWhenNotLoggedIn() throws Exception {
		Long enrollId = 1L;
		mockMvc.perform(delete("/enrolls/{enrollId}", enrollId))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value("작성자만 가능한 요청입니다"))
			.andDo(log());
	}
}
