package com.together.buytogether.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.member.dto.request.RegisterMemberDTO;
import com.together.buytogether.member.dto.request.SignInMemberDTO;
import com.together.buytogether.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static com.together.buytogether.member.domain.MemberFixture.aMember;
import static com.together.buytogether.member.domain.RegisterMemberDTOFixture.aRegisterMemberDTO;
import static com.together.buytogether.member.domain.SignInMemberDTOFixture.aSignInMemberDTO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
    }

    @Test
    @DisplayName("회원가입 성공")
    void registerMemberSuccess() throws Exception {
        RegisterMemberDTO registerMemberDTO = aRegisterMemberDTO().build();

        mockMvc.perform(post("/members")
                        .content(objectMapper.writeValueAsString(registerMemberDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());
    }

    @Test
    @DisplayName("회원가입시 정보누락은 실패")
    void registerMemberWithInvalidDataFail() throws Exception {
        RegisterMemberDTO registerMemberDTO = aRegisterMemberDTO().email("").build();

        mockMvc.perform(post("/members")
                        .content(objectMapper.writeValueAsString(registerMemberDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(log());
    }

    @Test
    @DisplayName("로그인 성공")
    public void signInSuccess() throws Exception {
        SignInMemberDTO loginRequest = aSignInMemberDTO().build();
        Member existingMember = aMember().build();

        given(memberService.signIn(any(String.class), any(String.class)))
                .willReturn(existingMember);

        mockMvc.perform(post("/members/sign-in")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(memberService).should().signIn(loginRequest.email(), loginRequest.password());
    }

    @Test
    @DisplayName("로그인 실패")
    public void singInFail() throws Exception {
        SignInMemberDTO loginRequest = aSignInMemberDTO().build();

        given(memberService.signIn(any(String.class), any(String.class)))
                .willThrow(new CustomException(ErrorCode.INVALID_EMAIL));

        mockMvc.perform(post("/members/sign-in")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().is4xxClientError())
                .andDo(log());

    }

    @Test
    @DisplayName("로그아웃 성공")
    public void signOutSuccess() throws Exception {

        session.setAttribute(SessionConst.LOGIN_MEMBER, 1L);

        mockMvc.perform(post("/members/sign-out")
                        .session(session))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

    }


}
