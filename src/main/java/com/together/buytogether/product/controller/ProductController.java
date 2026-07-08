package com.together.buytogether.product.controller;

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
import com.together.buytogether.product.dto.request.RegisterProductDTO;
import com.together.buytogether.product.dto.request.UpdateProductDTO;
import com.together.buytogether.product.dto.response.ProductLikeResponseDTO;
import com.together.buytogether.product.dto.response.ProductResponseDTO;
import com.together.buytogether.product.dto.response.RegisterProductResponseDTO;
import com.together.buytogether.product.dto.response.UpdateProductResponseDTO;
import com.together.buytogether.product.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {
	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@LoginRequired
	@PostMapping
	public ResponseEntity<ResponseDTO<RegisterProductResponseDTO>> registerProduct(
		@RequestBody @Valid RegisterProductDTO registerProductDTO,
		@LoginUser Long memberId) {
		return ResponseEntity.status(HttpStatus.CREATED).body(productService.registerProduct(memberId, registerProductDTO));
	}

	@GetMapping
	public ResponseEntity<ResponseDTO<List<ProductResponseDTO>>> getProducts() {
		return ResponseEntity.status(HttpStatus.OK).body(productService.getProducts());
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ResponseDTO<ProductResponseDTO>> getProduct(@PathVariable Long productId) {
		return ResponseEntity.status(HttpStatus.OK).body(productService.getProduct(productId));
	}

	@LoginRequired
	@PutMapping("/{productId}")
	public ResponseEntity<ResponseDTO<UpdateProductResponseDTO>> updateProduct(
		@LoginUser Long memberId,
		@PathVariable Long productId,
		@RequestBody @Valid UpdateProductDTO updateProductDTO) {
		return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(memberId, productId, updateProductDTO));
	}

	@LoginRequired
	@DeleteMapping("/{productId}")
	public ResponseEntity<ResponseDTO<String>> deleteProduct(
		@LoginUser Long memberId,
		@PathVariable Long productId) {
		return ResponseEntity.status(HttpStatus.OK).body(productService.deleteProduct(memberId, productId));
	}

	@LoginRequired
	@PostMapping("/{productId}/like")
	public ResponseEntity<ResponseDTO<ProductLikeResponseDTO>> likeProduct(
		@LoginUser Long memberId,
		@PathVariable Long productId) {
		return ResponseEntity.status(HttpStatus.CREATED).body(productService.likeProduct(memberId, productId));
	}
}
