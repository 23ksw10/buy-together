package com.together.buytogether.post.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostLike;
import com.together.buytogether.post.domain.PostLikeRepository;
import com.together.buytogether.post.domain.PostLikeStatus;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.post.domain.PostStatus;
import com.together.buytogether.post.dto.request.RegisterPostDTO;
import com.together.buytogether.post.dto.request.UpdatePostDTO;
import com.together.buytogether.post.dto.response.PostLikeResponseDTO;
import com.together.buytogether.post.dto.response.PostResponseDTO;
import com.together.buytogether.post.dto.response.RegisterPostResponseDTO;
import com.together.buytogether.post.dto.response.UpdatePostResponseDTO;

@Service
public class PostService {
	private final PostRepository postRepository;
	private final CommonMemberService commonMemberService;
	private final CommonPostService commonPostService;
	private final PostLikeRepository postLikeRepository;

	public PostService(
		PostRepository postRepository,
		CommonMemberService commonMemberService,
		CommonPostService commonPostService,
		PostLikeRepository postLikeRepository) {
		this.postRepository = postRepository;
		this.commonMemberService = commonMemberService;
		this.commonPostService = commonPostService;
		this.postLikeRepository = postLikeRepository;
	}

	@Transactional
	public ResponseDTO<RegisterPostResponseDTO> registerPost(Long memberId, RegisterPostDTO registerPostDTO) {
		Member member = commonMemberService.getMember(memberId);
		Post post = postRepository.save(registerPostDTO.toDomain(member));
		return ResponseDTO.successResult(RegisterPostResponseDTO.builder()
			.postId(post.getPostId())
			.name(post.getMember().getName())
			.content(post.getContent())
			.title(post.getTitle())
			.createdAt(LocalDateTime.now())
			.build());
	}

	@Transactional
	public ResponseDTO<PostLikeResponseDTO> likePost(Long memberId, Long postId) {
		Member member = commonMemberService.getMember(memberId);
		Post post = commonPostService.getPost(postId);
		if (post.getStatus() == PostStatus.CLOSED) {
			throw new CustomException(ErrorCode.POST_CLOSED);
		}
		Optional<PostLike> optionalPostLike = postLikeRepository.findByMemberIdAndPostId(memberId, postId);
		if (optionalPostLike.isPresent()) {
			PostLike postLike = optionalPostLike.get();
			if (postLike.getPostLikeStatus() == PostLikeStatus.OPEN) {
				postLike.delete();
				return ResponseDTO.successResult(PostLikeResponseDTO.builder()
					.postId(postId)
					.memberId(memberId)
					.isDeleted(true)
					.build());
			}

			postLike.active();

		} else {
			PostLike postLike = new PostLike(member, post, PostLikeStatus.OPEN);
			postLikeRepository.save(postLike);
		}
		return ResponseDTO.successResult(PostLikeResponseDTO.builder()
			.postId(postId)
			.memberId(memberId)
			.isDeleted(false)
			.build());
	}

	@CacheEvict(key = "#postId", value = "post")
	@Transactional
	public ResponseDTO<UpdatePostResponseDTO> updatePost(Long memberId, Long postId, UpdatePostDTO updatePostDTO) {
		Post post = commonPostService.getPost(postId);
		post.checkOwner(memberId);
		post.update(
			updatePostDTO.title(),
			updatePostDTO.content(),
			updatePostDTO.status(),
			updatePostDTO.expiredAt()
		);
		return ResponseDTO.successResult(UpdatePostResponseDTO.builder()
			.postId(postId)
			.memberName(post.getMember().getName())
			.content(post.getContent())
			.title(post.getTitle())
			.updatedAt(LocalDateTime.now())
			.build());
	}

	@CacheEvict(key = "#postId", value = "post")
	@Transactional
	public ResponseDTO<String> deletePost(Long memberId, Long postId) {
		Post post = commonPostService.getPost(postId);
		post.checkOwner(memberId);
		post.deletePost();
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
