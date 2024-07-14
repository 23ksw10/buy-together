package com.together.buytogether.post.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.together.buytogether.annotation.LoginRequired;
import com.together.buytogether.annotation.LoginUser;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.post.dto.request.RegisterProductDTO;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import com.together.buytogether.post.dto.response.PostResponseDTO;
import com.together.buytogether.post.dto.response.RegisterPostResponseDTO;
import com.together.buytogether.post.dto.response.RegisterProductResponseDTO;
import com.together.buytogether.post.dto.response.UpdatePostResponseDTO;
import com.together.buytogether.post.service.PostService;
import com.together.buytogether.post.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/posts")
public class PostController {
	private final PostService postService;
	private final ProductService productService;

	public PostController(
		PostService postService,
		ProductService productService
	) {
		this.postService = postService;
		this.productService = productService;
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
	public ResponseEntity<ResponseDTO<RegisterPostResponseDTO>> registerPost(
		@RequestBody @Valid RegisterPostDTO registerPostDTO,
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

	@LoginRequired
	@PostMapping("/{postId}")
	public ResponseEntity<ResponseDTO<RegisterProductResponseDTO>> registerProduct(@LoginUser Long memberID,
		@PathVariable Long postId,
		@RequestBody @Valid RegisterProductDTO registerProductDTO) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(productService.registerProduct(memberID, postId, registerProductDTO));
	}
}
