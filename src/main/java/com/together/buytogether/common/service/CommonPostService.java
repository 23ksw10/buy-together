package com.together.buytogether.common.service;

import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class CommonPostService {
    PostRepository postRepository;

    public CommonPostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post getPost(Long postId) {
        return postRepository.getByPostId(postId);
    }
}
