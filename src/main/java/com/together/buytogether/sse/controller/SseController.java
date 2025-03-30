package com.together.buytogether.sse.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.together.buytogether.sse.service.StockAlertService;

@RestController
@RequestMapping("/api/sse")
public class SseController {
	private final StockAlertService stockAlertService;

	public SseController(StockAlertService stockAlertService) {
		this.stockAlertService = stockAlertService;
	}

	@GetMapping("/stock-alerts")
	public SseEmitter subscribeToStockAlerts(
		@RequestParam Long userId,
		@RequestParam List<Long> productIds) {
		return stockAlertService.subscribe(userId, productIds);
	}

}
