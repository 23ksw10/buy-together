package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
class DeletePost {

    PostRepository postRepository;

    public DeletePost(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @DeleteMapping("/posts/{postId}")
    @Transactional
    public void request(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
                        @PathVariable Long postId) {
        Post post = postRepository.getByPostId(postId);
        post.checkOwner(memberId);
        postRepository.delete(post);
    }
}
