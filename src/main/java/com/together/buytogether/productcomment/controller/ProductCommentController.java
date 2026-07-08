package com.together.buytogether.productcomment.controller;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.together.buytogether.annotation.LoginRequired;
import com.together.buytogether.annotation.LoginUser;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.productcomment.dto.request.CommentDTO;
import com.together.buytogether.productcomment.dto.response.CommentResponseDTO;
import com.together.buytogether.productcomment.dto.response.RegisterCommentResponseDTO;
import com.together.buytogether.productcomment.dto.response.UpdateCommentResponseDTO;
import com.together.buytogether.productcomment.service.ProductCommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products/{productId}/comments")
public class ProductCommentController {
	private final ProductCommentService productCommentService;

	public ProductCommentController(ProductCommentService productCommentService) {
		this.productCommentService = productCommentService;
	}

	@GetMapping
	public ResponseEntity<ResponseDTO<List<CommentResponseDTO>>> getAllComment(@PathVariable Long productId) {
		return ResponseEntity.status(HttpStatus.OK).body(productCommentService.getProductComments(productId));
	}

	@GetMapping("/{commentId}")
	public ResponseEntity<ResponseDTO<CommentResponseDTO>> getComment(@PathVariable Long commentId) {
		return ResponseEntity.status(HttpStatus.OK).body(productCommentService.getProductComment(commentId));
	}

	@PostMapping
	@LoginRequired
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<ResponseDTO<RegisterCommentResponseDTO>> registerComment(
		@LoginUser Long memberId,
		@PathVariable Long productId,
		@RequestBody @Valid CommentDTO commentDTO) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(productCommentService.registerComment(memberId, productId, commentDTO));
	}

	@PutMapping("/{commentId}")
	@LoginRequired
	public ResponseEntity<ResponseDTO<UpdateCommentResponseDTO>> updateComment(
		@LoginUser Long memberId,
		@PathVariable @Valid Long commentId,
		@RequestBody @Valid CommentDTO commentDTO) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(productCommentService.updateComment(memberId, commentId, commentDTO));
	}

	@DeleteMapping("/{commentId}")
	@LoginRequired
	public ResponseEntity<ResponseDTO<String>> deleteComment(
		@LoginUser Long memberId,
		@PathVariable Long commentId) {
		return ResponseEntity.status(HttpStatus.OK).body(productCommentService.deleteComment(memberId, commentId));
	}
}
