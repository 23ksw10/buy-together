package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
public class UpdatePost {
    PostRepository postRepository;

    public UpdatePost(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @PutMapping("/posts/{postId}/update")
    @Transactional
    public void request(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
                        @PathVariable Long postId,
                        @Valid @RequestBody UpdatePostDTO updatePostDTO) {
        Post post = postRepository.getByPostId(postId);
        System.out.println(memberId);
        post.checkOwner(memberId);
        post.update(
                updatePostDTO.title(),
                updatePostDTO.content(),
                updatePostDTO.status(),
                updatePostDTO.expiredAt()
        );
    }

}
