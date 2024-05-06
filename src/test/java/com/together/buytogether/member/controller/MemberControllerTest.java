package com.together.buytogether.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.together.buytogether.member.domain.Address;
import com.together.buytogether.member.domain.Gender;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.member.dto.request.RegisterMemberDTO;
import com.together.buytogether.member.dto.request.SignInMemberDTO;
import com.together.buytogether.member.service.MemberService;
import com.together.buytogether.member.utils.HashingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

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
    void signUp_success() throws Exception {
        RegisterMemberDTO registerMemberDTO = RegisterMemberDTO.builder()
                .loginId("loginId")
                .name("name")
                .gender(Gender.MALE)
                .password("password")
                .phoneNumber("010-0000-0000")
                .address("경기도")
                .detailAddress("고양시")
                .build();

        mockMvc.perform(post("/members")
                        .content(objectMapper.writeValueAsString(registerMemberDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());
    }

    @Test
    @DisplayName("회원가입시 정보누락은 실패")
    void signUp_fail() throws Exception {
        RegisterMemberDTO registerMemberDTO = RegisterMemberDTO.builder()
                .loginId("loginId")
                .name("name")
                .gender(Gender.MALE)
                .password("password")
                .address("경기도")
                .detailAddress("고양시")
                .build();

        mockMvc.perform(post("/members")
                        .content(objectMapper.writeValueAsString(registerMemberDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(log());
    }

    @Test
    @DisplayName("로그인 성공")
    public void signIn_success() throws Exception {
        SignInMemberDTO loginRequest = new SignInMemberDTO("test", "test1");
        Member existingMember = createMember();

        given(memberService.signIn(any(String.class), any(String.class)))
                .willReturn(Optional.of(existingMember));

        mockMvc.perform(post("/members/sign-in")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(memberService).should().signIn("test", HashingUtil.encrypt("test1"));
    }

    @Test
    @DisplayName("로그인 실패")
    public void signIn_fail() throws Exception {
        SignInMemberDTO loginRequest = new SignInMemberDTO("test", "test1");

        given(memberService.signIn(any(String.class), any(String.class)))
                .willReturn(Optional.empty());

        mockMvc.perform(post("/members/sign-in")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().is4xxClientError())
                .andDo(log());

    }

    @Test
    @DisplayName("로그아웃 성공")
    public void signOut_fail() throws Exception {

        session.setAttribute(SessionConst.LOGIN_MEMBER, "loginId");

        mockMvc.perform(post("/members/sign-out")
                        .session(session))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

    }

    private Member createMember() {
        return Member.builder()
                .loginId("test")
                .name("test")
                .gender(Gender.MALE)
                .phoneNumber("010-0000-0000")
                .password("test1")
                .address(new Address("경기도", "고양시"))
                .build();
    }

}
