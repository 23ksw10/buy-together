package com.together.buytogether.post.service;

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
import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostLike;
import com.together.buytogether.post.domain.PostLikeJdbcRepository;
import com.together.buytogether.post.domain.PostLikeRepository;
import com.together.buytogether.post.domain.PostLikeStatus;
import com.together.buytogether.post.domain.PostStatus;
import com.together.buytogether.post.dto.request.LikeRequestDTO;

@Service
public class AsyncLikeService {

	private final BlockingQueue<LikeRequestDTO> likeQueue = new LinkedBlockingQueue<>();
	private final PostLikeRepository postLikeRepository;
	private final CommonMemberService commonMemberService;
	private final CommonPostService commonPostService;
	private final ThreadPoolTaskExecutor workerThreadPool;
	private final PostLikeJdbcRepository postLikeJdbcRepository;
	private final int BULK_SIZE = 30;
	private final long TIMEOUT_MS = 2000;

	public AsyncLikeService(
		PostLikeRepository postLikeRepository,
		CommonMemberService commonMemberService,
		CommonPostService commonPostService,
		ThreadPoolTaskExecutor workerThreadPool,
		PostLikeJdbcRepository postLikeJdbcRepository
	) {
		this.postLikeRepository = postLikeRepository;
		this.commonMemberService = commonMemberService;
		this.commonPostService = commonPostService;
		this.workerThreadPool = workerThreadPool;
		this.postLikeJdbcRepository = postLikeJdbcRepository;
		startLeaderThread();
	}

	public void enqueueLike(Long memberId, Long postId) {
		likeQueue.offer(new LikeRequestDTO(memberId, postId));
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
		System.out.println(requests.size());
		for (LikeRequestDTO req : requests) {
			Member member = commonMemberService.getMember(req.memberId());
			Post post = commonPostService.getPost(req.postId());
			if (post.getStatus() == PostStatus.CLOSED) {
				throw new CustomException(ErrorCode.POST_CLOSED);
			}

			Optional<PostLike> optionalPostLike = postLikeRepository.findByMemberIdAndPostId(req.memberId(),
				req.postId());
			if (optionalPostLike.isPresent()) {
				PostLike postLike = optionalPostLike.get();
				postLike.changeStatus();
			} else {
				PostLike postLike = PostLike.builder()
					.post(post)
					.member(member)
					.postLikeStatus(PostLikeStatus.OPEN).build();
				postLikeRepository.save(postLike);
			}
		}

	}

	public void bulkProcessLike(List<LikeRequestDTO> batch) {
		int[] results = postLikeJdbcRepository.batchInsertPostLikes(batch);
		long successCount = Arrays.stream(results).filter(r -> r >= 1).count();
	}
}


