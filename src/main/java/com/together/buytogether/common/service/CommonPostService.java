package com.together.buytogether.common.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;

@Service
public class CommonPostService {
	private final PostRepository postRepository;

	public CommonPostService(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	@Cacheable(key = "#postId", value = "post")
	public Post getPost(Long postId) {
		return postRepository.getByPostId(postId);
	}
}
