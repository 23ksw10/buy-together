package com.together.buytogether.post.controller;

import com.together.buytogether.annotation.LoginRequired;
import com.together.buytogether.annotation.LoginUser;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import com.together.buytogether.post.dto.response.PostResponseDTO;
import com.together.buytogether.post.dto.response.RegisterPostResponseDTO;
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

    @LoginRequired
    @PutMapping("/{postId}")
    public ResponseEntity<ResponseDTO<UpdatePostResponseDTO>> updatePost(@LoginUser Long memberId,
                                                                         @PathVariable Long postId,
                                                                         @Valid @RequestBody UpdatePostDTO updatePostDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.updatePost(memberId, postId, updatePostDTO));
    }

    @LoginRequired
    @PostMapping
    public ResponseEntity<ResponseDTO<RegisterPostResponseDTO>> registerPost(@RequestBody @Valid RegisterPostDTO registerPostDTO,
                                                                             @LoginUser Long memberId) {
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

    @LoginRequired
    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseDTO<String>> deletePost(@LoginUser Long memberId,
                                                          @PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.deletePost(memberId, postId));
    }
}
