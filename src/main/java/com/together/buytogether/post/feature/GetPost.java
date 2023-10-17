package com.together.buytogether.post.feature;

import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.post.dto.response.PostResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public List<PostResponseDTO> getPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(p -> new PostResponseDTO(
                        p.getMember().getName(),
                        p.getPostId(),
                        p.getTitle(),
                        p.getContent(),
                        p.getExpiredAt().toString()
                ))
                .toList();
    }

    @GetMapping("/posts/{postId}")
    public PostResponseDTO getPost(@PathVariable Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 게시글입니다"));
        return new PostResponseDTO(
                post.getMember().getName(),
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getExpiredAt().toString()
        );
    }

}
