package com.together.buytogether.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.product.dto.response.TrendingProductResponseDTO;
import com.together.buytogether.product.service.TrendingProductService;

@RestController
@RequestMapping("/group-buys/trending")
public class TrendingProductController {
	private final TrendingProductService trendingProductService;

	public TrendingProductController(TrendingProductService trendingProductService) {
		this.trendingProductService = trendingProductService;
	}

	@GetMapping("/v1")
	public ResponseEntity<ResponseDTO<List<TrendingProductResponseDTO>>> getTrendingV1() {
		return ResponseEntity.status(HttpStatus.OK)
			.body(ResponseDTO.successResult(trendingProductService.getTrendingV1()));
	}

	@GetMapping("/v2")
	public ResponseEntity<ResponseDTO<List<TrendingProductResponseDTO>>> getTrendingV2() {
		return ResponseEntity.status(HttpStatus.OK)
			.body(ResponseDTO.successResult(trendingProductService.getTrendingV2()));
	}

	@GetMapping("/v3")
	public ResponseEntity<ResponseDTO<List<TrendingProductResponseDTO>>> getTrendingV3() {
		return ResponseEntity.status(HttpStatus.OK)
			.body(ResponseDTO.successResult(trendingProductService.getTrendingV3()));
	}
}
