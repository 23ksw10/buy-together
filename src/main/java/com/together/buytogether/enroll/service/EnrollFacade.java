package com.together.buytogether.enroll.service;

import static com.together.buytogether.common.lock.LockNumberConstants.*;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import com.together.buytogether.common.lock.DistributedLockFacade;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.enroll.dto.request.CancelEnrollDTO;
import com.together.buytogether.enroll.dto.request.JoinEnrollDTO;
import com.together.buytogether.enroll.dto.response.JoinEnrollResponseDTO;
import com.together.buytogether.enroll.dto.response.RecentEnrollInfoDto;

@Service
public class EnrollFacade {

	private final EnrollService enrollService;
	private final DistributedLockFacade distributedLockFacade;

	public EnrollFacade(EnrollService enrollService, DistributedLockFacade distributedLockFacade) {
		this.enrollService = enrollService;
		this.distributedLockFacade = distributedLockFacade;
	}

	public ResponseDTO<JoinEnrollResponseDTO> joinBuying(Long memberId, @NotNull JoinEnrollDTO joinEnrollDTO) {
		return distributedLockFacade.executeWithLock("enroll_lock_" + joinEnrollDTO.productId(),
			LOCK_WAIT_MILLI_SECOND,
			LOCK_LEASE_MILLI_SECOND,
			() -> enrollService.joinBuying(memberId, joinEnrollDTO));
	}

	public void optimisticJoinBuying(Long memberId, JoinEnrollDTO joinEnrollDTO) throws InterruptedException {
		int retryCount = 0;
		while (true) {
			try {
				enrollService.joinBuying(memberId, joinEnrollDTO);
				break;
			} catch (Exception e) {
				Thread.sleep(50);
			} finally {
				retryCount++;
				if (retryCount > 10) {
					throw new RuntimeException("실패");
				}
			}
		}
	}

	public ResponseDTO<String> cancelBuying(Long memberId, CancelEnrollDTO cancelEnrollDTO, Long enrollId) {
		return distributedLockFacade.executeWithLock("enroll_lock_" + cancelEnrollDTO.productId(),
			LOCK_WAIT_MILLI_SECOND,
			LOCK_LEASE_MILLI_SECOND,
			() -> enrollService.cancelBuying(memberId, enrollId));
	}

	public ResponseDTO<List<RecentEnrollInfoDto>> getRecentEnrolls(Long memberId) {
		return enrollService.getRecentEnrolls(memberId);
	}
}
