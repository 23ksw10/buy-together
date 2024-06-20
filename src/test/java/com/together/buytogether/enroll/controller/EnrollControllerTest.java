package com.together.buytogether.enroll.controller;

import com.together.buytogether.enroll.service.EnrollService;
import com.together.buytogether.member.domain.SessionConst;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@DisplayName("Enroll Controller 테스트")
@WebMvcTest(controllers = EnrollController.class)
public class EnrollControllerTest {

    @MockBean
    EnrollService enrollService;
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

        then(enrollService).should().joinBuying(memberId, postId);

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

        then(enrollService).should().cancelBuying(memberId, postId);

    }

    @Test
    @DisplayName("로그인 되어있지 않다면 구매취소를 할 수 없다")
    void cannotCancelBuyingWhenNotLoggedIn() throws Exception {
        mockMvc.perform(delete("/posts/{postId}/enrolls", postId))
                .andExpect(status().isUnauthorized())
                .andDo(log());

    }
}
