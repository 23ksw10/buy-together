package com.together.buytogether.post.feature;

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
        return postService.getPosts();
    }

    @GetMapping("/posts/{postId}")
    public PostResponseDTO getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

}
