package com.together.buytogether.enroll.service;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonPostService;
import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EnrollService {
    private final CommonMemberService commonMemberService;
    private final CommonPostService commonPostService;
    private final EnrollRepository enrollRepository;

    public EnrollService(
            CommonMemberService commonMemberService,
            CommonPostService commonPostService,
            EnrollRepository enrollRepository) {
        this.commonMemberService = commonMemberService;
        this.commonPostService = commonPostService;
        this.enrollRepository = enrollRepository;
    }


    @Transactional
    public void joinBuying(Long memberId, Long postId) {
        Member member = commonMemberService.getMember(memberId);
        Post post = commonPostService.getPost(postId);
        if (isAlreadyEnrolled(memberId, postId)) {
            throw new CustomException(ErrorCode.ENROLL_ALREADY_DONE);
        }
        Enroll enroll = new Enroll(member, post, LocalDateTime.now());
        post.increaseJoinCount();
        enrollRepository.save(enroll);
    }

    @Transactional
    public void cancelBuying(Long memberId, Long postId) {
        Post post = commonPostService.getPost(postId);
        Enroll enroll = enrollRepository.findByMemberIdAndPostId(memberId, postId).orElseThrow(() -> {
            throw new CustomException(ErrorCode.ENROLL_NOT_FOUND);
        });
        post.decreaseJoinCount();
        enrollRepository.delete(enroll);
    }

    private boolean isAlreadyEnrolled(Long memberId, Long postId) {
        return enrollRepository.findByMemberIdAndPostId(memberId, postId).isPresent();
    }

}
