package com.together.buytogether.enroll.service;

import static com.together.buytogether.common.lock.LockNumberConstants.*;

import org.springframework.stereotype.Service;

import com.together.buytogether.common.lock.DistributedLockFacade;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.enroll.dto.response.JoinEnrollResponseDTO;

@Service
public class EnrollFacade {

	private final EnrollService enrollService;
	private final DistributedLockFacade distributedLockFacade;

	public EnrollFacade(EnrollService enrollService, DistributedLockFacade distributedLockFacade) {
		this.enrollService = enrollService;
		this.distributedLockFacade = distributedLockFacade;
	}

	public ResponseDTO<JoinEnrollResponseDTO> joinBuying(Long memberId, Long postId) {
		return distributedLockFacade.executeWithLock("enroll_lock_" + postId, LOCK_WAIT_MILLI_SECOND,
			LOCK_LEASE_MILLI_SECOND,
			() -> enrollService.joinBuying(memberId, postId));
	}

	public ResponseDTO<String> cancelBuying(Long memberId, Long postId) {
		return distributedLockFacade.executeWithLock("enroll_lock_" + postId, LOCK_WAIT_MILLI_SECOND,
			LOCK_LEASE_MILLI_SECOND,
			() -> enrollService.cancelBuying(memberId, postId));
	}
}
