package com.together.buytogether.postcomment.service;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.postcomment.domain.PostComment;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import com.together.buytogether.postcomment.dto.request.CommentDTO;
import com.together.buytogether.postcomment.dto.response.CommentResponseDTO;
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
    public void registerComment(Long memberId, Long postId, CommentDTO commentDTO) {
        Member member = commonMemberService.getMember(memberId);
        Post post = commonPostService.getPost(postId);
        PostComment postComment = commentDTO.toDomain(member, post);
        postCommentRepository.save(postComment);
    }

    @Transactional(readOnly = true)
    public CommentResponseDTO getPostComment(Long commentId) {
        PostComment postComment = postCommentRepository.getByCommentId(commentId);
        return new CommentResponseDTO(
                postComment.getCommentId(),
                postComment.getPost().getPostId(),
                postComment.getMember().getName(),
                postComment.getContent(),
                postComment.getCreatedAt().toString(),
                postComment.getUpdatedAt().toString()
        );
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getPostComments(Long postId) {
        List<PostComment> comments = postCommentRepository.findAllByPostId(postId);
        return comments.stream()
                .map(c -> new CommentResponseDTO(
                        c.getCommentId(),
                        c.getPost().getPostId(),
                        c.getMember().getName(),
                        c.getContent(),
                        c.getCreatedAt().toString(),
                        c.getUpdatedAt().toString()
                ))
                .toList();
    }

    @Transactional
    public void updateComment(Long memberId, Long commentId, CommentDTO commentDTO) {
        PostComment postComment = postCommentRepository.getByCommentId(commentId);
        checkOwner(memberId, postComment);
        postComment.update(
                commentDTO.content(),
                LocalDateTime.now());
    }

    @Transactional
    public void deleteComment(Long memberId, Long commentId) {
        PostComment postComment = postCommentRepository.getByCommentId(commentId);
        checkOwner(memberId, postComment);
        postCommentRepository.delete(postComment);
    }

    public void checkOwner(Long memberId, PostComment postComment) {
        if (!postComment.checkOwner(memberId)) {
            throw new CustomException(ErrorCode.IS_NOT_OWNER);
        }
    }

}
