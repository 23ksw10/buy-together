package com.together.buytogether.enroll.feature;

import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.member.domain.SessionConst;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
public class CancelBuying {
    private final EnrollRepository enrollRepository;

    private final PostRepository postRepository;

    public CancelBuying(EnrollRepository enrollRepository, PostRepository postRepository) {
        this.enrollRepository = enrollRepository;
        this.postRepository = postRepository;
    }

    @DeleteMapping("posts/{postId}/enrolls")
    public void request(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable Long postId) {
        Post post = postRepository.getByPostId(postId);
        Enroll enroll = enrollRepository.getEnroll(memberId, postId);
        post.decreaseJoinCount();
        enrollRepository.delete(enroll);
    }

}
