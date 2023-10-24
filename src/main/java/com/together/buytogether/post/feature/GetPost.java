package com.together.buytogether.post.feature;

import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.dto.response.PostResponseDTO;
import com.together.buytogether.post.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetPost {
    private final PostService postService;


    public GetPost(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public List<PostResponseDTO> getPosts() {
        List<Post> posts = postService.getAllPosts();
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
        Post post = postService.getPost(postId);
        return new PostResponseDTO(
                post.getMember().getName(),
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getExpiredAt().toString()
        );
    }

}
