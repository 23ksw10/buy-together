package com.together.buytogether.post.feature;

import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import jakarta.validation.Valid;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
class UpdatePost {
    PostRepository postRepository;

    public UpdatePost(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @PutMapping("/posts/{postId}/update")
    public void request(@CookieValue("JSESSIONID") Long memberId,
                        @PathVariable Long postId,
                        @Valid @RequestBody Request request) {
        Post post = postRepository.getByPostId(postId);
        post.checkOwner(memberId);
        post.update(
                request.title,
                request.content,
                request.expiredAt
        );
    }

    public record Request(
            String title,
            String content,
            LocalDateTime expiredAt) {
        public Request {
            Assert.hasText(title, "글 제목은 필수입니다");
            Assert.hasText(content, "글 내용은 필수입니다");
            Assert.notNull(expiredAt, "글 만료일은 필수입니다");
        }
    }
}
