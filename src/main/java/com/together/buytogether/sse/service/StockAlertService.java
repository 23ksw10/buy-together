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

	// SSE 연결을 저장할 맵 (productId -> 이미터 집합)
	private final Map<Long, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

	// 알림 구독 등록
	public SseEmitter subscribe(Long userId, List<Long> productIds) {
		SseEmitter emitter = new SseEmitter(180000L); // 3분 타임아웃

		// 사용자별로 관심 상품 등록
		for (Long productId : productIds) {
			emitters.computeIfAbsent(productId, k -> new CopyOnWriteArraySet<>()).add(emitter);
		}

		// 연결 종료 시 정리
		emitter.onCompletion(() -> removeEmitter(productIds, emitter));
		emitter.onTimeout(() -> removeEmitter(productIds, emitter));
		emitter.onError(e -> removeEmitter(productIds, emitter));

		// 초기 연결 확인 이벤트 전송
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

	// 제품 재고 체크 및 알림 발송
	public void checkAndNotifyLowStock(Product product) {
		// 남은 재고 계산
		Long remainingStock = product.getMaxQuantity() - product.getSoldQuantity();

		// 재고가 10개 이하인 경우 알림 발송
		if (remainingStock <= 10) {
			// Redis에 최근 알림 시간 확인 (중복 알림 방지)
			String cacheKey = "product:lowstock:" + product.getProductId();
			String lastNotifiedStr = redisTemplate.opsForValue().get(cacheKey);

			// 마지막 알림 후 30분 지났거나 첫 알림인 경우만 발송
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

			// 비정상 이미터 제거
			productEmitters.removeAll(deadEmitters);
		}
	}

}
