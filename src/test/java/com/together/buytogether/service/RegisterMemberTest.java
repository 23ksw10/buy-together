package com.together.buytogether.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;


public class RegisterMemberTest {
    private RegisterMember registerMember;

    @BeforeEach
    void setUp() {
        registerMember = new RegisterMember();
    }

    @Test
    @DisplayName("회원가입")
    void registerMember() {
        String address = "경기도 고양시 덕양구 화정로 27";
        String detailAddress = "625동 1004호";
        RegisterMember.Request request = new RegisterMember.Request(
                "name",
                "loginId",
                "password",
                "phoneNumber",
                SEX.MALE,
                address, //도로명 주소
                detailAddress    //상세 주소
        );
        //when
        registerMember.request(request);

        //then
//        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }

    public enum SEX {
        MALE("남자"),
        FEMALE("여자");

        private final String description;

        SEX(String description) {
            this.description = description;
        }
    }

    public static class Address {
        private final String address;
        private final String detailAddress;

        public Address(String address, String detailAddress) {
            validateAddress(address, detailAddress);
            this.address = address;
            this.detailAddress = detailAddress;
        }

        private void validateAddress(String address, String detailAddress) {
            Assert.hasText(address, "주소는 필수 값입니다");
            Assert.hasText(detailAddress, "상세 주소는 필수 값입니다");
        }
    }

    public static class Member {
        private final String name;
        private final String loginId;
        private final String password;
        private final String phoneNumber;
        private final SEX sex;
        private final Address address;

        public Member(
                String name,
                String loginId,
                String password,
                String phoneNumber,
                SEX sex,
                Address address) {
            validateMember(name, loginId, password, phoneNumber, sex, address);
            this.name = name;
            this.loginId = loginId;
            this.password = password;
            this.phoneNumber = phoneNumber;
            this.sex = sex;
            this.address = address;
        }

        private void validateMember(
                String name,
                String loginId,
                String password,
                String phoneNumber,
                SEX sex,
                Address address) {
            Assert.hasText(name, "이름은 필수 값입니다");
            Assert.hasText(loginId, "로그인 아이디는 필수 값입니다");
            Assert.hasText(password, "비밀번호는 필수 값입니다");
            Assert.hasText(phoneNumber, "전화번호는 필수 값입니다");
            Assert.notNull(sex, "성별은 필수 값입니다");
            Assert.notNull(address, "주소는 필수 값입니다");
        }

    }

    private class RegisterMember {


        public void request(Request request) {
            // request에서 필요한 값들을 꺼내서 회원 도메인을 생성하고 저장한
            Member member = request.toDomain();
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
}
