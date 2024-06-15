package com.together.buytogether.post.controller;

import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import com.together.buytogether.post.dto.response.PostResponseDTO;
import com.together.buytogether.post.dto.response.UpdatePostResponseDTO;
import com.together.buytogether.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ResponseDTO<UpdatePostResponseDTO>> updatePost(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
                                                                         @PathVariable Long postId,
                                                                         @Valid @RequestBody UpdatePostDTO updatePostDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.updatePost(memberId, postId, updatePostDTO));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<PostResponseDTO>> registerPost(@RequestBody @Valid RegisterPostDTO registerPostDTO,
                                                                     @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.registerPost(memberId, registerPostDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<PostResponseDTO>>> getPosts() {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPosts());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ResponseDTO<PostResponseDTO>> getPost(@PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseDTO<String>> deletePost(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
                                                          @PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.deletePost(memberId, postId));
    }
}
