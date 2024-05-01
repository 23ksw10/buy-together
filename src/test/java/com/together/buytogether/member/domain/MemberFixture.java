package com.together.buytogether.member.domain;

public class MemberFixture {

    private Long memberId = 1L;

    private String name = "name";

    private String loginId = "loginId";

    private String password = "password";

    private String phoneNumber = "phoneNumber";

    private Gender gender = Gender.MALE;
    private AddressFixture address = AddressFixture.aAddress();


    public static MemberFixture aMember() {
        return new MemberFixture();
    }

    public MemberFixture memberId(Long memberId) {
        this.memberId = memberId;
        return this;
    }

    public MemberFixture name(String name) {
        this.name = name;
        return this;
    }

    public MemberFixture loginId(String loginId) {
        this.loginId = loginId;
        return this;
    }

    public MemberFixture password(String password) {
        this.password = password;
        return this;
    }

    public MemberFixture phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public MemberFixture sex(Gender gender) {
        this.gender = gender;
        return this;
    }

    public MemberFixture address(AddressFixture address) {
        this.address = address;
        return this;
    }

    public Member build() {
        return new Member(
                name,
                loginId,
                password,
                phoneNumber,
                Gender.MALE,
                address.build()
        );
    }
}