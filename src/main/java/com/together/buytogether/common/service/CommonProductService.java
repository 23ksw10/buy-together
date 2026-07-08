package com.together.buytogether.common.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductRepository;

@Service
public class CommonProductService {
	private final ProductRepository productRepository;

	public CommonProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	// @SingleFlightCacheable(cacheName = "SingleProduct", key = "#productId",
	// 	redisTimeToLiveMillis = 20000, localTimeToLiveMillis = 10000, decisionForUpdate = 70)
	@Transactional(readOnly = true)
	public Product getProduct(Long productId) {
		return productRepository.getByProductId(productId);
	}
}
