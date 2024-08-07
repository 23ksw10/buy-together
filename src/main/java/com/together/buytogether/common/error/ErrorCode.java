package com.together.buytogether.common.error;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	/* 400 BAD_REQUEST : 잘못된 요청 */
	INVALID_EMAIL(BAD_REQUEST, "잘못된 이메일입니다"),
	INVALID_PASSWORD(BAD_REQUEST, "잘못된 비밀번호입니다"),

	/* 403 FORBIDDEN : 권한이 없는 요청  */
	IS_NOT_OWNER(UNAUTHORIZED, "작성자만 가능한 요청입니다"),

	/* 404 NOT_FOUND : 요청 대상이 존재하지 않을 때 */
	MEMBER_NOT_FOUND(NOT_FOUND, "존재하지 않는 멤버입니다"),
	POST_NOT_FOUND(NOT_FOUND, "해당 게시글이 존재하지 않습니다"),
	COMMENT_NOT_FOUND(NOT_FOUND, "해당 댓글이 존재하지 않습니다"),
	ENROLL_NOT_FOUND(NOT_FOUND, "등록 정보가 존재하지 않습니다"),
	PRODUCT_NOT_FOUND(NOT_FOUND, "판매 상품이 존재하지 않습니다"),

	/* 409 CONFLICT : 서버의 상태와 충돌하는 경우 */
	MEMBER_ALREADY_EXIST(CONFLICT, "이미 존재하는 회원입니다"),
	POST_CLOSED(CONFLICT, "이미 마감된 게시글입니다"),
	PRODUCT_QUANTITY_EXCEEDED(CONFLICT, "구매 한도가 초과 되었습니다"),
	ENROLL_ALREADY_DONE(CONFLICT, "이미 구매에 참여했습니다"),
	INVALID_QUANTITY(CONFLICT, "구매량은 음수가 될 수 없습니다"),
	ENROLL_MEMBER_NOT_BE_NEGATIVE(CONFLICT, "구매에 참여한 멤버는 음수가 될 수 없습니다");

	private final HttpStatus httpStatus;
	private final String message;
}
