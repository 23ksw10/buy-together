package com.together.buytogether.member.feature;

import com.together.buytogether.member.domain.Address;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SEX;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterMember {
    private final MemberRepository memberRepository;

    public RegisterMember(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @PostMapping("/members")
    @ResponseStatus(HttpStatus.CREATED)
    public void request(@RequestBody @Valid Request request) {
        // request에서 필요한 값들을 꺼내서 회원 도메인을 생성하고 저장한다
        // 중복 검사도
        validateDuplicateMember(request.loginId);
        Member member = request.toDomain();
        memberRepository.save(member);
    }

    private void validateDuplicateMember(String logingId) {
        memberRepository.findAll().stream()
                .filter(member -> member.getLoginId().equals(logingId))
                .findFirst()
                .ifPresent(member -> {
                    throw new IllegalArgumentException("이미 존재하는 회원입니다");
                });
    }

    public record Request(
            @NotBlank(message = "이름은 필수 값입니다")
            String name,
            @NotBlank(message = "로그인 아이디는 필수 값입니다")
            String loginId,
            @NotBlank(message = "비밀번호는 필수 값입니다")
            String password,
            @NotBlank(message = "전화번호는 필수 값입니다")
            String phoneNumber,
            @NotNull(message = "성별은 필수 값입니다")
            SEX sex,
            @NotBlank(message = "주소는 필수 값입니다")
            String address,
            @NotBlank(message = "상세 주소는 필수 값입니다")
            String detailAddress) {
        public Request {
            Assert.hasText(name, "이름은 필수 값입니다");
            Assert.hasText(loginId, "로그인 아이디는 필수 값입니다");
            Assert.hasText(password, "비밀번호는 필수 값입니다");
            Assert.hasText(phoneNumber, "전화번호는 필수 값입니다");
            Assert.notNull(sex, "성별은 필수 값입니다");
            Assert.hasText(address, "주소는 필수 값입니다");
            Assert.hasText(detailAddress, "상세 주소는 필수 값입니다");
        }

        public Member toDomain() {
            return new Member(
                    name,
                    loginId,
                    "password",
                    "phoneNumber",
                    sex,
                    new Address(address, detailAddress)
            );
        }
    }
}
