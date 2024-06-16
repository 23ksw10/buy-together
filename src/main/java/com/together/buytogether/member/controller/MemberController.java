package com.together.buytogether.member.controller;

import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.member.dto.request.RegisterMemberDTO;
import com.together.buytogether.member.dto.request.SignInMemberDTO;
import com.together.buytogether.member.dto.response.RegisterMemberResponseDTO;
import com.together.buytogether.member.dto.response.SignInMemberResponseDTO;
import com.together.buytogether.member.dto.response.SignOutMemberResponseDTO;
import com.together.buytogether.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;


    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping()
    public ResponseEntity<ResponseDTO<RegisterMemberResponseDTO>> signUp(@RequestBody @Valid RegisterMemberDTO registerMemberDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.registerMember(registerMemberDTO));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ResponseDTO<SignInMemberResponseDTO>> signIn(
            @RequestBody @Valid SignInMemberDTO signInMemberDTO,
            HttpServletRequest httpServletRequest) {
        Member logInMember = memberService.signIn(signInMemberDTO.email(), signInMemberDTO.password());
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute(SessionConst.LOGIN_MEMBER, logInMember.getMemberId());
        SignInMemberResponseDTO signInMemberResponseDTO = new SignInMemberResponseDTO(logInMember.getEmail(), logInMember.getName());
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.successResult(signInMemberResponseDTO));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<ResponseDTO> signOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.successResult(new SignOutMemberResponseDTO("SignOut Success")));
    }
}
