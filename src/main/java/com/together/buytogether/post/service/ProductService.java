package com.together.buytogether.post.service;

import org.springframework.stereotype.Service;

import com.together.buytogether.post.domain.ProductRepository;

@Service
public class ProductService {
	ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
}
