package com.together.buytogether.post.service;

import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import com.together.buytogether.post.dto.response.PostResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {
    PostRepository postRepository;
    CommonMemberService commonMemberService;
    CommonPostService commonPostService;

    public PostService(
            PostRepository postRepository,
            CommonMemberService commonMemberService,
            CommonPostService commonPostService) {
        this.postRepository = postRepository;
        this.commonMemberService = commonMemberService;
        this.commonPostService = commonPostService;
    }

    @Transactional
    public Post registerPost(Long memberId, RegisterPostDTO registerPostDTO) {
        Member member = commonMemberService.getMember(memberId);
        Post post = registerPostDTO.toDomain(member);
        return postRepository.save(post);
    }

    @Transactional
    public void updatePost(Long memberId, Long postId, UpdatePostDTO updatePostDTO) {
        Post post = commonPostService.getPost(postId);
        post.checkOwner(memberId);
        post.update(
                updatePostDTO.title(),
                updatePostDTO.content(),
                updatePostDTO.status(),
                updatePostDTO.expiredAt(),
                updatePostDTO.maxJoinCount()
        );
    }

    @Transactional
    public void deletePost(Long memberId, Long postId) {
        Post post = commonPostService.getPost(postId);
        post.checkOwner(memberId);
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public PostResponseDTO getPost(Long postId) {
        Post post = commonPostService.getPost(postId);
        return new PostResponseDTO(
                post.getMember().getName(),
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getExpiredAt().toString()
        );
    }

    @Transactional(readOnly = true)
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
}
