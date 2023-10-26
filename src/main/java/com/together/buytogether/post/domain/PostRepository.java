package com.together.buytogether.post.domain;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostRepository extends JpaRepository<Post, Long> {

    default Post getByPostId(Long postId) {
        return findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}
