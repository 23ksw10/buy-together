package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import com.together.buytogether.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class UpdatePost {
    PostService postService;

    public UpdatePost(PostService postService) {
        this.postService = postService;
    }

    @PutMapping("/posts/{postId}/update")
    public void request(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
                        @PathVariable Long postId,
                        @Valid @RequestBody UpdatePostDTO updatePostDTO) {
        postService.updatePost(memberId, postId, updatePostDTO);
    }

}
