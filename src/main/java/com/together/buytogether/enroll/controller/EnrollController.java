package com.together.buytogether.enroll.controller;

import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.enroll.dto.response.JoinEnrollResponseDTO;
import com.together.buytogether.enroll.service.EnrollService;
import com.together.buytogether.member.domain.SessionConst;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/enrolls")
public class EnrollController {
    private final EnrollService enrollService;

    public EnrollController(EnrollService enrollService) {
        this.enrollService = enrollService;
    }

    @DeleteMapping()
    public ResponseEntity<ResponseDTO<String>> cancel(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK).body(enrollService.cancelBuying(memberId, postId));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseDTO<JoinEnrollResponseDTO>> join(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollService.joinBuying(memberId, postId));
    }
}
