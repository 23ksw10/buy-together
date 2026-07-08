package com.together.buytogether.productcomment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonProductService;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.productcomment.domain.ProductComment;
import com.together.buytogether.productcomment.domain.ProductCommentRepository;
import com.together.buytogether.productcomment.dto.request.CommentDTO;
import com.together.buytogether.productcomment.dto.response.CommentResponseDTO;
import com.together.buytogether.productcomment.dto.response.RegisterCommentResponseDTO;
import com.together.buytogether.productcomment.dto.response.UpdateCommentResponseDTO;

@Service
public class ProductCommentService {
	private final ProductCommentRepository productCommentRepository;
	private final CommonProductService commonProductService;
	private final CommonMemberService commonMemberService;

	public ProductCommentService(
		ProductCommentRepository productCommentRepository,
		CommonProductService commonProductService,
		CommonMemberService commonMemberService) {
		this.productCommentRepository = productCommentRepository;
		this.commonProductService = commonProductService;
		this.commonMemberService = commonMemberService;
	}

	@Transactional
	public ResponseDTO<RegisterCommentResponseDTO> registerComment(Long memberId, Long productId, CommentDTO commentDTO) {
		Member member = commonMemberService.getMember(memberId);
		Product product = commonProductService.getProduct(productId);
		ProductComment productComment = commentDTO.toDomain(member, product);
		ProductComment savedComment = productCommentRepository.save(productComment);
		return ResponseDTO.successResult(RegisterCommentResponseDTO.builder()
			.commentId(savedComment.getCommentId())
			.productId(savedComment.getProduct().getProductId())
			.content(savedComment.getContent())
			.memberName(savedComment.getMember().getName())
			.createdAt(savedComment.getCreatedAt())
			.build());
	}

	@Transactional(readOnly = true)
	public ResponseDTO<CommentResponseDTO> getProductComment(Long commentId) {
		ProductComment productComment = productCommentRepository.getByCommentId(commentId);
		return ResponseDTO.successResult(CommentResponseDTO.builder()
			.commentId(productComment.getCommentId())
			.productId(productComment.getProduct().getProductId())
			.content(productComment.getContent())
			.memberName(productComment.getMember().getName())
			.createdAt(productComment.getCreatedAt())
			.updatedAt(productComment.getUpdatedAt())
			.build());
	}

	@Transactional(readOnly = true)
	public ResponseDTO<List<CommentResponseDTO>> getProductComments(Long productId) {
		List<ProductComment> comments = productCommentRepository.findAllByProductId(productId);
		List<CommentResponseDTO> commentsResponse = comments.stream()
			.map(c -> new CommentResponseDTO(
				c.getCommentId(),
				c.getProduct().getProductId(),
				c.getMember().getName(),
				c.getContent(),
				c.getCreatedAt(),
				c.getUpdatedAt()
			))
			.toList();
		return ResponseDTO.successResult(commentsResponse);
	}

	@Transactional
	public ResponseDTO<UpdateCommentResponseDTO> updateComment(Long memberId, Long commentId, CommentDTO commentDTO) {
		ProductComment productComment = productCommentRepository.getByCommentId(commentId);
		checkOwner(memberId, productComment);
		productComment.update(commentDTO.content(), LocalDateTime.now());
		return ResponseDTO.successResult(UpdateCommentResponseDTO.builder()
			.commentId(productComment.getCommentId())
			.productId(productComment.getProduct().getProductId())
			.currentContent(productComment.getContent())
			.memberName(productComment.getMember().getName())
			.updatedAt(productComment.getUpdatedAt())
			.build());
	}

	@Transactional
	public ResponseDTO<String> deleteComment(Long memberId, Long commentId) {
		ProductComment productComment = productCommentRepository.getByCommentId(commentId);
		checkOwner(memberId, productComment);
		productCommentRepository.delete(productComment);
		return ResponseDTO.successResult("성공적으로 댓글을 삭제했습니다");
	}

	public void checkOwner(Long memberId, ProductComment productComment) {
		if (!productComment.checkOwner(memberId)) {
			throw new CustomException(ErrorCode.IS_NOT_OWNER);
		}
	}
}
