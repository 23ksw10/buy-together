package com.together.buytogether.enroll.controller;

import com.together.buytogether.enroll.service.EnrollService;
import com.together.buytogether.member.domain.SessionConst;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/enrolls")
public class EnrollController {
    private final EnrollService enrollService;

    public EnrollController(EnrollService enrollService) {
        this.enrollService = enrollService;
    }

    @DeleteMapping()
    public void cancel(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable Long postId) {
        enrollService.cancelBuying(memberId, postId);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void join(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable Long postId) {
        enrollService.joinBuying(memberId, postId);
    }
}
