package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.member.domain.SessionManager;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.post.service.PostService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterPost {
    private final PostService postService;

    private final SessionManager sessionManger;

    public RegisterPost(
            PostService postService,
            SessionManager sessionManger) {
        this.postService = postService;
        this.sessionManger = sessionManger;
    }

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public void request(@RequestBody @Valid RegisterPostDTO registerPostDTO,
                        HttpServletRequest httpServletRequest) {
        Cookie cookie = httpServletRequest.getCookies()[0];
        Long memberId = (Long) sessionManger.getSession(cookie.getName()).getAttribute(SessionConst.LOGIN_MEMBER);
        postService.registerPost(memberId, registerPostDTO);
    }

}
