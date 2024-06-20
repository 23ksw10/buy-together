package com.together.buytogether.enroll.controller;

import com.together.buytogether.annotation.LoginRequired;
import com.together.buytogether.annotation.LoginUser;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.enroll.dto.response.JoinEnrollResponseDTO;
import com.together.buytogether.enroll.service.EnrollService;
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
    @LoginRequired
    public ResponseEntity<ResponseDTO<String>> cancel(
            @LoginUser Long memberId,
            @PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK).body(enrollService.cancelBuying(memberId, postId));
    }

    @PostMapping()
    @LoginRequired
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseDTO<JoinEnrollResponseDTO>> join(
            @LoginUser Long memberId,
            @PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollService.joinBuying(memberId, postId));
    }
}
