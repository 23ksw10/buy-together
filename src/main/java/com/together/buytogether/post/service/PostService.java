package com.together.buytogether.post.service;

import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import com.together.buytogether.post.dto.response.PostResponseDTO;
import com.together.buytogether.post.dto.response.UpdatePostResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public ResponseDTO<PostResponseDTO> registerPost(Long memberId, RegisterPostDTO registerPostDTO) {
        Member member = commonMemberService.getMember(memberId);
        Post post = registerPostDTO.toDomain(member);
        return ResponseDTO.successResult(PostResponseDTO.builder()
                .postId(post.getPostId())
                .memberName(post.getMember().getName())
                .content(post.getContent())
                .title(post.getTitle())
                .expiredAt(post.getExpiredAt())
                .build());
    }

    @Transactional
    public ResponseDTO<UpdatePostResponseDTO> updatePost(Long memberId, Long postId, UpdatePostDTO updatePostDTO) {
        Post post = commonPostService.getPost(postId);
        post.checkOwner(memberId);
        post.update(
                updatePostDTO.title(),
                updatePostDTO.content(),
                updatePostDTO.status(),
                updatePostDTO.expiredAt(),
                updatePostDTO.maxJoinCount()
        );
        return ResponseDTO.successResult(UpdatePostResponseDTO.builder()
                .postId(postId)
                .memberName(post.getMember().getName())
                .content(post.getContent())
                .title(post.getTitle())
                .joinCount(post.getJoinCount())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    @Transactional
    public ResponseDTO<String> deletePost(Long memberId, Long postId) {
        Post post = commonPostService.getPost(postId);
        post.checkOwner(memberId);
        postRepository.delete(post);
        return ResponseDTO.successResult("성공적으로 게시글을 삭제했습니다");
    }

    @Transactional(readOnly = true)
    public ResponseDTO<PostResponseDTO> getPost(Long postId) {
        Post post = commonPostService.getPost(postId);
        return ResponseDTO.successResult(PostResponseDTO.builder()
                .postId(post.getPostId())
                .memberName(post.getMember().getName())
                .content(post.getContent())
                .title(post.getTitle())
                .expiredAt(post.getExpiredAt())
                .build());
    }

    @Transactional(readOnly = true)
    public ResponseDTO<List<PostResponseDTO>> getPosts() {
        List<Post> posts = postRepository.findAll();
        List<PostResponseDTO> allPosts = posts.stream()
                .map(p -> new PostResponseDTO(
                        p.getMember().getName(),
                        p.getPostId(),
                        p.getTitle(),
                        p.getContent(),
                        p.getExpiredAt()
                ))
                .toList();
        return ResponseDTO.successResult(allPosts);
    }
}
