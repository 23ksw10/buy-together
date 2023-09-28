package com.together.buytogether.post.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PostRepository {
    private final Map<Long, Post> posts = new HashMap<>();
    private Long id = 1L;

    public void save(Post post) {
        post.assignId(id++);
        posts.put(post.getId(), post);
    }

    public List<Post> findAll() {
        return new ArrayList<>(posts.values());
    }
}
