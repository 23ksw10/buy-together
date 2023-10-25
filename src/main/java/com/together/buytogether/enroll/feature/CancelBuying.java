package com.together.buytogether.enroll.feature;

import com.together.buytogether.enroll.service.EnrollService;
import com.together.buytogether.member.domain.SessionConst;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
public class CancelBuying {
    private final EnrollService enrollService;


    public CancelBuying(EnrollService enrollService) {
        this.enrollService = enrollService;
    }

    @DeleteMapping("posts/{postId}/enrolls")
    public void request(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable Long postId) {
        enrollService.cancelBuying(memberId, postId);
    }

}
