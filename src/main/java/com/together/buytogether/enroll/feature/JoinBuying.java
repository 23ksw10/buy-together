package com.together.buytogether.enroll.feature;

import com.together.buytogether.enroll.service.EnrollService;
import com.together.buytogether.member.domain.SessionConst;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class JoinBuying {
    private final EnrollService enrollService;

    public JoinBuying(EnrollService enrollService) {
        this.enrollService = enrollService;
    }

    @PostMapping("/posts/{postId}/enroll")
    @ResponseStatus(HttpStatus.CREATED)
    public void request(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable Long postId) {
        enrollService.joinBuying(memberId, postId);
    }
}
