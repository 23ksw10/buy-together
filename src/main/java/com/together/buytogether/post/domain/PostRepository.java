package com.together.buytogether.post.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface PostRepository extends JpaRepository<Post, Long> {

    default Post getByPostId(Long postId) {
        return findById(postId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 게시글입니다. %d".formatted(postId)));
    }
}
