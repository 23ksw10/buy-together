package com.together.buytogether.common.service;

import org.springframework.stereotype.Service;

import com.together.buytogether.post.domain.Product;
import com.together.buytogether.post.domain.ProductRepository;

@Service
public class CommonProductService {
	private final ProductRepository productRepository;

	public CommonProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public Product getProduct(Long productId) {
		return productRepository.getByProductId(productId);
	}
}
