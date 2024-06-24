package com.together.buytogether.enroll.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.together.buytogether.annotation.LoginRequired;
import com.together.buytogether.annotation.LoginUser;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.enroll.dto.response.JoinEnrollResponseDTO;
import com.together.buytogether.enroll.service.EnrollFacade;

@RestController
@RequestMapping("/posts/{postId}/enrolls")
public class EnrollController {
	private final EnrollFacade enrollFacade;

	public EnrollController(EnrollFacade enrollFacade) {
		this.enrollFacade = enrollFacade;
	}

	@DeleteMapping()
	@LoginRequired
	public ResponseEntity<ResponseDTO<String>> cancel(
		@LoginUser Long memberId,
		@PathVariable Long postId) {
		return ResponseEntity.status(HttpStatus.OK).body(enrollFacade.cancelBuying(memberId, postId));
	}

	@PostMapping()
	@LoginRequired
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<ResponseDTO<JoinEnrollResponseDTO>> join(
		@LoginUser Long memberId,
		@PathVariable Long postId) {
		return ResponseEntity.status(HttpStatus.CREATED).body(enrollFacade.joinBuying(memberId, postId));
	}
}
