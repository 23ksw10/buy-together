package com.together.buytogether.enroll.domain;

import java.util.List;

import com.together.buytogether.enroll.dto.response.RecentEnrollInfoDto;

public interface CustomizeEnrollRepository {
	List<RecentEnrollInfoDto> getRecentEnrolls(Long memberId);
}
