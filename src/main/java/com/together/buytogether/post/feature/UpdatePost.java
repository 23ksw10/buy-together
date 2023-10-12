package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.post.domain.PostStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public
class UpdatePost {
    PostRepository postRepository;

    public UpdatePost(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @PutMapping("/posts/{postId}/update")
    @Transactional
    public void request(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
                        @PathVariable Long postId,
                        @Valid @RequestBody Request request) {
        Post post = postRepository.getByPostId(postId);
        System.out.println(memberId);
        post.checkOwner(memberId);
        post.update(
                request.title,
                request.content,
                request.status,
                request.expiredAt
        );
    }

    public record Request(
            @NotBlank(message = "글 제목은 필수입니다")
            String title,
            @NotBlank(message = "글 내용은 필수입니다")
            String content,

            @NotNull(message = "글 상태는 필수입니다")
            PostStatus status,
            @NotNull(message = "글 만료일은 필수입니다")
            LocalDateTime expiredAt) {

    }
}
