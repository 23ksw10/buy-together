package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetPost {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public GetPost(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    @GetMapping("/posts")
    public List<PostResponse> getPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(p -> new PostResponse(
                        p.getMember().getName(),
                        p.getPostId(),
                        p.getTitle(),
                        p.getContent(),
                        p.getExpiredAt().toString()
                ))
                .toList();
    }

    private record PostResponse(
            String memberName,
            Long postId,
            String title,
            String content,
            String expiredAt) {

    }
}
