package com.together.buytogether.enroll.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.enroll.dto.response.JoinEnrollResponseDTO;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;

@Service
public class EnrollService {
	private final CommonMemberService commonMemberService;
	private final CommonPostService commonPostService;
	private final EnrollRepository enrollRepository;

	private final PostRepository postRepository;

	public EnrollService(
		CommonMemberService commonMemberService,
		CommonPostService commonPostService,
		EnrollRepository enrollRepository,
		PostRepository postRepository) {
		this.commonMemberService = commonMemberService;
		this.commonPostService = commonPostService;
		this.enrollRepository = enrollRepository;
		this.postRepository = postRepository;
	}

	@Transactional
	public ResponseDTO<JoinEnrollResponseDTO> joinBuying(Long memberId, Long postId) {
		Member member = commonMemberService.getMember(memberId);
		Post post = commonPostService.getPost(postId);
		if (isAlreadyEnrolled(memberId, postId)) {
			throw new CustomException(ErrorCode.ENROLL_ALREADY_DONE);
		}
		post.increaseJoinCount();
		Enroll enroll = new Enroll(member, post);
		Enroll savedEnroll = enrollRepository.save(enroll);
		return ResponseDTO.successResult(JoinEnrollResponseDTO.builder()
			.postId(postId)
			.postTitle(post.getTitle())
			.enrollId(savedEnroll.getEnrollId())
			.memberName(member.getName())
			.memberId(memberId)
			.sellerName(post.getMember().getName())
			.joinedAt(savedEnroll.getCreatedAt())
			.build());
	}

	@Transactional
	public ResponseDTO<String> cancelBuying(Long memberId, Long postId) {
		Post post = commonPostService.getPost(postId);
		Enroll enroll = enrollRepository.getEnroll(memberId, postId);
		post.decreaseJoinCount();
		enrollRepository.delete(enroll);
		return ResponseDTO.successResult("구매가 취소되셨습니다");
	}

	private boolean isAlreadyEnrolled(Long memberId, Long postId) {
		return enrollRepository.findByMemberIdAndPostId(memberId, postId).isPresent();
	}

	private Post findPostWithPessimistic(Long postId) {
		return postRepository.findWithPessimisticByPostId(postId).orElseThrow(
			() -> new CustomException(ErrorCode.POST_NOT_FOUND));
	}

}
