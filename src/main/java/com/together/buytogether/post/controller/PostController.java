package com.together.buytogether.post.controller;

import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import com.together.buytogether.post.dto.response.PostResponseDTO;
import com.together.buytogether.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(
            PostService postService
    ) {
        this.postService = postService;
    }

    @PutMapping("/{postId}")
    public void updatePost(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
                           @PathVariable Long postId,
                           @Valid @RequestBody UpdatePostDTO updatePostDTO) {
        postService.updatePost(memberId, postId, updatePostDTO);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registerPost(@RequestBody @Valid RegisterPostDTO registerPostDTO,
                             @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
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
