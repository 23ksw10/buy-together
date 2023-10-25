package com.together.buytogether.post.controller;

import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.member.domain.SessionManager;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import com.together.buytogether.post.dto.response.PostResponseDTO;
import com.together.buytogether.post.service.PostService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final SessionManager sessionManger;

    public PostController(
            PostService postService,
            SessionManager sessionManger) {
        this.postService = postService;
        this.sessionManger = sessionManger;
    }

    @PutMapping("/{postId}/update")
    public void updatePost(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
                           @PathVariable Long postId,
                           @Valid @RequestBody UpdatePostDTO updatePostDTO) {
        postService.updatePost(memberId, postId, updatePostDTO);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registerPost(@RequestBody @Valid RegisterPostDTO registerPostDTO,
                             HttpServletRequest httpServletRequest) {
        Cookie cookie = httpServletRequest.getCookies()[0];
        Long memberId = (Long) sessionManger.getSession(cookie.getName()).getAttribute(SessionConst.LOGIN_MEMBER);
        postService.registerPost(memberId, registerPostDTO);
    }

    @GetMapping
    public List<PostResponseDTO> getPosts() {
        return postService.getPosts();
    }

    @GetMapping("/{postId}")
    public PostResponseDTO getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
                           @PathVariable Long postId) {
        postService.deletePost(memberId, postId);
    }
}
