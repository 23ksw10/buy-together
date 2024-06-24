package com.together.buytogether.enroll.controller;

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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.together.buytogether.enroll.service.EnrollFacade;
import com.together.buytogether.member.domain.SessionConst;

@DisplayName("Enroll Controller 테스트")
@WebMvcTest(controllers = EnrollController.class)
public class EnrollControllerTest {

	@MockBean
	EnrollFacade enrollFacade;
	Long postId;
	Long memberId;
	MockHttpSession mockSession;
	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		postId = 1L;
		memberId = 1L;
		mockSession = new MockHttpSession();
		mockSession.setAttribute(SessionConst.LOGIN_MEMBER, 1L);
	}

	@Test
	@DisplayName("구매에 참여할 수 있다")
	void joinBuying() throws Exception {
		mockMvc.perform(post("/posts/{postId}/enrolls", postId)
				.session(mockSession))
			.andExpect(status().is2xxSuccessful())
			.andDo(log());

		then(enrollFacade).should().joinBuying(memberId, postId);

	}

	@Test
	@DisplayName("로그인 되어있지 않다면 구매에 참여할 수 없다")
	void cannotJoinBuyingWhenNotLoggedIn() throws Exception {
		mockMvc.perform(post("/posts/{postId}/enrolls", postId))
			.andExpect(status().isUnauthorized())
			.andDo(log());

	}

	@Test
	@DisplayName("구매를 취소할 수 있다")
	void cancelBuying() throws Exception {
		mockMvc.perform(delete("/posts/{postId}/enrolls", postId)
				.session(mockSession))
			.andExpect(status().is2xxSuccessful())
			.andDo(log());

		then(enrollFacade).should().cancelBuying(memberId, postId);

	}

	@Test
	@DisplayName("로그인 되어있지 않다면 구매취소를 할 수 없다")
	void cannotCancelBuyingWhenNotLoggedIn() throws Exception {
		mockMvc.perform(delete("/posts/{postId}/enrolls", postId))
			.andExpect(status().isUnauthorized())
			.andDo(log());

	}
}
