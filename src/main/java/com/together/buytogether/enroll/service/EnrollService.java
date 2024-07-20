package com.together.buytogether.enroll.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonProductService;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.enroll.dto.request.JoinEnrollDTO;
import com.together.buytogether.enroll.dto.response.JoinEnrollResponseDTO;
import com.together.buytogether.enroll.dto.response.RecentEnrollInfoDto;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Product;

@Service
public class EnrollService {
	private final CommonMemberService commonMemberService;
	private final CommonProductService commonProductService;
	private final EnrollRepository enrollRepository;

	public EnrollService(
		CommonMemberService commonMemberService,
		CommonProductService commonProductService,
		EnrollRepository enrollRepository) {
		this.commonMemberService = commonMemberService;
		this.commonProductService = commonProductService;
		this.enrollRepository = enrollRepository;
	}

	@Transactional
	public ResponseDTO<JoinEnrollResponseDTO> joinBuying(Long memberId, JoinEnrollDTO joinEnrollDTO) {
		Member member = commonMemberService.getMember(memberId);
		Product product = commonProductService.getProduct(joinEnrollDTO.productId());
		if (isAlreadyEnrolled(memberId, joinEnrollDTO.productId())) {
			throw new CustomException(ErrorCode.ENROLL_ALREADY_DONE);
		}
		product.increaseSoldQuantity(joinEnrollDTO.quantity());
		Enroll enroll = new Enroll(member, product, joinEnrollDTO.quantity());
		Enroll savedEnroll = enrollRepository.save(enroll);
		return ResponseDTO.successResult(JoinEnrollResponseDTO.builder()
			.postId(product.getPost().getPostId())
			.postTitle(product.getPost().getTitle())
			.enrollId(savedEnroll.getEnrollId())
			.memberName(member.getName())
			.memberId(memberId)
			.sellerName(product.getPost().getMember().getName())
			.joinedAt(savedEnroll.getCreatedAt())
			.build());
	}

	@Transactional
	public ResponseDTO<String> cancelBuying(Long memberId, Long enrollId) {
		Enroll enroll = enrollRepository.getEnroll(enrollId);
		validateCancellation(memberId, enroll);
		enroll.getProduct().decreaseSoldQuantity(enroll.getQuantity());
		enrollRepository.delete(enroll);
		return ResponseDTO.successResult("구매가 취소되셨습니다");
	}

	public ResponseDTO<List<RecentEnrollInfoDto>> getRecentEnrolls(Long memberId) {
		return ResponseDTO.successResult(enrollRepository.getRecentEnrolls(memberId));
	}

	private boolean isAlreadyEnrolled(Long memberId, Long productId) {
		return enrollRepository.findByMemberIdAndProductId(memberId, productId).isPresent();
	}

	private void validateCancellation(Long memberId, Enroll enroll) {
		if (!enroll.getMember().getMemberId().equals(memberId)) {
			throw new CustomException(ErrorCode.IS_NOT_OWNER);
		}
	}

}
