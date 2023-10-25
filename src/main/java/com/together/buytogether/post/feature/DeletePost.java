package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.service.PostService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
class DeletePost {

    private final PostService postService;

    public DeletePost(PostService postService) {
        this.postService = postService;
    }

    @DeleteMapping("/posts/{postId}")
    public void request(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
                        @PathVariable Long postId) {
        postService.deletePost(memberId, postId);
    }
}
