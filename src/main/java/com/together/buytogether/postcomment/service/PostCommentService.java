package com.together.buytogether.postcomment.service;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.postcomment.domain.PostComment;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import com.together.buytogether.postcomment.dto.request.CommentDTO;
import com.together.buytogether.postcomment.dto.response.CommentResponseDTO;
import com.together.buytogether.postcomment.dto.response.RegisterCommentResponseDTO;
import com.together.buytogether.postcomment.dto.response.UpdateCommentResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final CommonPostService commonPostService;
    private final CommonMemberService commonMemberService;

    public PostCommentService(
            PostCommentRepository postCommentRepository,
            CommonPostService commonPostService,
            CommonMemberService commonMemberService) {
        this.postCommentRepository = postCommentRepository;
        this.commonPostService = commonPostService;
        this.commonMemberService = commonMemberService;
    }

    @Transactional
    public ResponseDTO<RegisterCommentResponseDTO> registerComment(Long memberId, Long postId, CommentDTO commentDTO) {
        Member member = commonMemberService.getMember(memberId);
        Post post = commonPostService.getPost(postId);
        PostComment postComment = commentDTO.toDomain(member, post);
        PostComment savedComment = postCommentRepository.save(postComment);
        return ResponseDTO.successResult(RegisterCommentResponseDTO.builder()
                .commentId(savedComment.getCommentId())
                .postId(savedComment.getPost().getPostId())
                .content(savedComment.getContent())
                .memberName(savedComment.getMember().getName())
                .createdAt(savedComment.getCreatedAt())
                .build());
    }

    @Transactional(readOnly = true)
    public ResponseDTO<CommentResponseDTO> getPostComment(Long commentId) {
        PostComment postComment = postCommentRepository.getByCommentId(commentId);
        return ResponseDTO.successResult(CommentResponseDTO.builder()
                .commentId(postComment.getCommentId())
                .postId(postComment.getPost().getPostId())
                .content(postComment.getContent())
                .memberName(postComment.getMember().getName())
                .createdAt(postComment.getCreatedAt())
                .updatedAt(postComment.getUpdatedAt())
                .build());
    }

    @Transactional(readOnly = true)
    public ResponseDTO<List<CommentResponseDTO>> getPostComments(Long postId) {
        List<PostComment> comments = postCommentRepository.findAllByPostId(postId);
        List<CommentResponseDTO> commentsResponse = comments.stream()
                .map(c -> new CommentResponseDTO(
                        c.getCommentId(),
                        c.getPost().getPostId(),
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
        PostComment postComment = postCommentRepository.getByCommentId(commentId);
        checkOwner(memberId, postComment);
        postComment.update(
                commentDTO.content(),
                LocalDateTime.now());
        return ResponseDTO.successResult(UpdateCommentResponseDTO.builder()
                .commentId(postComment.getCommentId())
                .postId(postComment.getPost().getPostId())
                .currentContent(postComment.getContent())
                .memberName(postComment.getMember().getName())
                .updatedAt(postComment.getUpdatedAt())
                .build());
    }

    @Transactional
    public ResponseDTO<String> deleteComment(Long memberId, Long commentId) {
        PostComment postComment = postCommentRepository.getByCommentId(commentId);
        checkOwner(memberId, postComment);
        postCommentRepository.delete(postComment);
        return ResponseDTO.successResult("성공적으로 댓글을 삭제했습니다");
    }

    public void checkOwner(Long memberId, PostComment postComment) {
        if (!postComment.checkOwner(memberId)) {
            throw new CustomException(ErrorCode.IS_NOT_OWNER);
        }
    }

}
