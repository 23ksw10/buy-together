
# 스프링부트로 백엔드 API 연습해보기

## 스펙

- Java 17
- Spring boot 3.1

## 요구사항

- 공동구매 할 수 서비스를 생각하며 만들어보았습니다.

### 회원

- [ ] 회원가입을 할 수 있다.
- [ ] 로그인 할 수 있다.
- [ ] 로그아웃 할 수 있다.


### 게시글

- [ ] 게시글을 생성 할 수 있다.
- [ ] 게시글을 업데이트 할 수 있다.
    - [ ] 게시글의 생성자만이 수정할 수 있다.
    - [ ] 게시글의 상태가 CLOSED일 경우 수정할 수 없다.
- [ ] 게시글을 조회할 수 있다.
- [ ] 게시글을 삭제할 수 있다.

### 댓글

- [ ] 댓글을 생성 할 수 있다.
- [ ] 댓글을 업데이트 할 수 있다.
    - [ ] 댓글의 생성자만이 수정할 수 있다.
    - [ ] 게시글의 상태가 CLOSED일 경우 수정할 수 없다.
- [ ] 댓글을 조회할 수 있다.
- [ ] 댓글을 삭제할 수 있다.

### 구매 참여

- [ ] 구매 참여 할 수 있다.
    - [ ] 구매는 한번만 참여할 수 있다.
    - [ ] 게시글의 구매참여자가 1증가한다.
- [ ] 구매를 취소 할 수 있다.
    - [ ] 게시글의 구매참여자가 1감소한다.


