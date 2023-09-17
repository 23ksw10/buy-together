package com.together.buytogether.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.util.Assert;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Comment("회원")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    @Comment("회원 아이디")
    private Long memberId;
    @Column(name = "name", nullable = false)
    @Comment("이름")
    private String name;
    @Column(name = "login_id", nullable = false, unique = true)
    @Comment("로그인 아이디")
    private String loginId;
    @Column(name = "password", nullable = false)
    @Comment("비밀번호")
    private String password;
    @Column(name = "phone_number", nullable = false)
    @Comment("전화번호")
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false)
    @Comment("성별")
    private SEX sex;
    @Embedded
    private Address address;

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
