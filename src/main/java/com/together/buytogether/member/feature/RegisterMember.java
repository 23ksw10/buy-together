package com.together.buytogether.member.feature;

import com.together.buytogether.member.domain.Address;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SEX;
import org.springframework.util.Assert;

class RegisterMember {
    private final MemberRepository memberRepository;

    public RegisterMember(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void request(Request request) {
        // request에서 필요한 값들을 꺼내서 회원 도메인을 생성하고 저장한
        Member member = request.toDomain();
        memberRepository.save(member);
    }

    public record Request(
            String name,
            String loginId,
            String password,
            String phoneNumber,
            SEX sex,
            String address,
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
