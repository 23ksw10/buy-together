package com.together.buytogether.product.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonProductService;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductLike;
import com.together.buytogether.product.domain.ProductLikeJdbcRepository;
import com.together.buytogether.product.domain.ProductLikeRepository;
import com.together.buytogether.product.domain.ProductLikeStatus;
import com.together.buytogether.product.domain.ProductStatus;
import com.together.buytogether.product.dto.request.LikeRequestDTO;

@Service
public class AsyncLikeService {

	private final BlockingQueue<LikeRequestDTO> likeQueue = new LinkedBlockingQueue<>();
	private final ProductLikeRepository productLikeRepository;
	private final CommonMemberService commonMemberService;
	private final CommonProductService commonProductService;
	private final ThreadPoolTaskExecutor workerThreadPool;
	private final ProductLikeJdbcRepository productLikeJdbcRepository;
	private final int BULK_SIZE = 30;
	private final long TIMEOUT_MS = 2000;

	public AsyncLikeService(
		ProductLikeRepository productLikeRepository,
		CommonMemberService commonMemberService,
		CommonProductService commonProductService,
		ThreadPoolTaskExecutor workerThreadPool,
		ProductLikeJdbcRepository productLikeJdbcRepository
	) {
		this.productLikeRepository = productLikeRepository;
		this.commonMemberService = commonMemberService;
		this.commonProductService = commonProductService;
		this.workerThreadPool = workerThreadPool;
		this.productLikeJdbcRepository = productLikeJdbcRepository;
		startLeaderThread();
	}

	public void enqueueLike(Long memberId, Long productId) {
		likeQueue.offer(new LikeRequestDTO(memberId, productId));
	}

	private void startLeaderThread() {
		Thread leaderThread = new Thread(() -> {
			List<LikeRequestDTO> buffer = new ArrayList<>();
			long lastFlushTime = System.currentTimeMillis();

			while (true) {
				try {
					LikeRequestDTO req = likeQueue.poll(100, TimeUnit.MILLISECONDS);
					if (req != null) {
						buffer.add(req);
					}

					long now = System.currentTimeMillis();
					boolean needFlush = buffer.size() >= BULK_SIZE || (now - lastFlushTime) >= TIMEOUT_MS;

					if (needFlush && !buffer.isEmpty()) {
						List<LikeRequestDTO> batch = new ArrayList<>(buffer);
						buffer.clear();
						lastFlushTime = now;

						workerThreadPool.execute(() -> bulkProcessLike(batch));
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});
		leaderThread.setDaemon(true);
		leaderThread.start();
	}

	@Transactional
	public void processLike(List<LikeRequestDTO> requests) {
		for (LikeRequestDTO req : requests) {
			Member member = commonMemberService.getMember(req.memberId());
			Product product = commonProductService.getProduct(req.productId());
			if (product.getStatus() == ProductStatus.CLOSED) {
				throw new CustomException(ErrorCode.PRODUCT_CLOSED);
			}

			Optional<ProductLike> optionalProductLike = productLikeRepository.findByMemberIdAndProductId(
				req.memberId(),
				req.productId());
			if (optionalProductLike.isPresent()) {
				ProductLike productLike = optionalProductLike.get();
				productLike.changeStatus();
			} else {
				ProductLike productLike = ProductLike.builder()
					.product(product)
					.member(member)
					.productLikeStatus(ProductLikeStatus.OPEN)
					.build();
				productLikeRepository.save(productLike);
			}
		}
	}

	public void bulkProcessLike(List<LikeRequestDTO> batch) {
		int[] results = productLikeJdbcRepository.batchInsertProductLikes(batch);
		Arrays.stream(results).filter(r -> r >= 1).count();
	}
}
