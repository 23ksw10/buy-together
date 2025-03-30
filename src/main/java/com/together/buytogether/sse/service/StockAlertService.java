package com.together.buytogether.sse.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.together.buytogether.post.domain.Product;
import com.together.buytogether.sse.dto.StockAlertDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockAlertService {
	private final RedisTemplate<String, String> redisTemplate;

	private final Map<Long, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

	public SseEmitter subscribe(Long userId, List<Long> productIds) {
		SseEmitter emitter = new SseEmitter(180000L); // 3분 타임아웃

		for (Long productId : productIds) {
			emitters.computeIfAbsent(productId, k -> new CopyOnWriteArraySet<>()).add(emitter);
		}

		emitter.onCompletion(() -> removeEmitter(productIds, emitter));
		emitter.onTimeout(() -> removeEmitter(productIds, emitter));
		emitter.onError(e -> removeEmitter(productIds, emitter));

		try {
			emitter.send(SseEmitter.event()
				.name("connect")
				.data("재고 알림 서비스에 연결되었습니다."));
		} catch (IOException e) {
			emitter.complete();
		}

		return emitter;
	}

	private void removeEmitter(List<Long> productIds, SseEmitter emitter) {
		for (Long productId : productIds) {
			Set<SseEmitter> productEmitters = emitters.get(productId);
			if (productEmitters != null) {
				productEmitters.remove(emitter);
				if (productEmitters.isEmpty()) {
					emitters.remove(productId);
				}
			}
		}
	}

	public void checkAndNotifyLowStock(Product product) {
		// 남은 재고 계산
		Long remainingStock = product.getMaxQuantity() - product.getSoldQuantity();

		if (remainingStock <= 10) {
			String cacheKey = "product:lowstock:" + product.getProductId();
			String lastNotifiedStr = redisTemplate.opsForValue().get(cacheKey);

			boolean shouldNotify = lastNotifiedStr == null ||
				LocalDateTime.parse(lastNotifiedStr).plusMinutes(30).isBefore(LocalDateTime.now());

			if (shouldNotify) {
				sendStockAlert(product);
				redisTemplate.opsForValue().set(cacheKey, LocalDateTime.now().toString());
			}
		}
	}

	private void sendStockAlert(Product product) {
		StockAlertDTO alert = new StockAlertDTO(product);
		Set<SseEmitter> productEmitters = emitters.get(product.getProductId());

		if (productEmitters != null && !productEmitters.isEmpty()) {
			List<SseEmitter> deadEmitters = new ArrayList<>();

			for (SseEmitter emitter : productEmitters) {
				try {
					emitter.send(SseEmitter.event()
						.name("stockAlert")
						.data(alert));
				} catch (IOException e) {
					deadEmitters.add(emitter);
				}
			}

			productEmitters.removeAll(deadEmitters);
		}
	}

}
