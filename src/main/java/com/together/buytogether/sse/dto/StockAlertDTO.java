package com.together.buytogether.sse.dto;

import java.time.LocalDateTime;

import com.together.buytogether.product.domain.Product;

public record StockAlertDTO(
	Long productId,
	String productName,
	Long remainingStock,
	Long maxQuantity,
	LocalDateTime timestamp

) {
	public StockAlertDTO(Product product) {
		this(
			product.getProductId(),
			product.getTitle(),
			product.getMaxQuantity() - product.getSoldQuantity(),
			product.getMaxQuantity(),
			LocalDateTime.now()
		);
	}
}
