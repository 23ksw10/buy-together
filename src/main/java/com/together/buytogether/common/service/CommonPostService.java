package com.together.buytogether.common.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.together.buytogether.annotation.SingleFlightCacheable;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;

@Service
public class CommonPostService {
	private final PostRepository postRepository;

	public CommonPostService(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	@SingleFlightCacheable(cacheName = "SinglePost", key = "#postId",
		redisTimeToLiveMillis = 20000, localTimeToLiveMillis = 10000, decisionForUpdate = 70)
	@Transactional(readOnly = true)
	public Post getPost(Long postId) {
		return postRepository.getByPostId(postId);
	}
}
