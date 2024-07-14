package com.together.buytogether.post.service;

import org.springframework.stereotype.Service;

import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.Product;
import com.together.buytogether.post.domain.ProductRepository;
import com.together.buytogether.post.dto.request.RegisterProductDTO;
import com.together.buytogether.post.dto.response.RegisterProductResponseDTO;

@Service
public class ProductService {
	private final ProductRepository productRepository;
	private final CommonPostService commonPostService;

	public ProductService(
		ProductRepository productRepository,
		CommonPostService commonPostService) {
		this.productRepository = productRepository;
		this.commonPostService = commonPostService;
	}

	public ResponseDTO<RegisterProductResponseDTO> registerProduct(Long memberId, Long postId,
		RegisterProductDTO registerProductDTO) {
		Post post = commonPostService.getPost(postId);
		post.checkOwner(memberId);
		Product product = Product.builder()
			.price(registerProductDTO.price())
			.soldQuantity(registerProductDTO.soldQuantity())
			.maxQuantity(registerProductDTO.maxQuantity())
			.post(post)
			.build();
		Product savedProduct = productRepository.save(product);
		return ResponseDTO.successResult(RegisterProductResponseDTO.builder()
			.productId(savedProduct.getProductId())
			.price(savedProduct.getPrice())
			.postId(postId)
			.build());
	}
}
