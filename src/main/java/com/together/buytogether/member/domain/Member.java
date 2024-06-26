package com.together.buytogether.member.domain;

import com.google.common.annotations.VisibleForTesting;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
    @Column(name = "email", nullable = false, unique = true)
    @Comment("로그인 이메일")
    private String email;
    @Column(name = "password", nullable = false)
    @Comment("비밀번호")
    private String password;
    @Column(name = "phone_number", nullable = false)
    @Comment("전화번호")
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false)
    @Comment("성별")
    private Gender gender;
    @Embedded
    private Address address;

    @Builder
    public Member(
            String name,
            String email,
            String password,
            String phoneNumber,
            Gender gender,
            Address address) {
        validateMember(name, email, password, phoneNumber, gender, address);
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.address = address;
    }


    @VisibleForTesting
    public void setId(Long id) {
        this.memberId = id;
    }


    private void validateMember(
            String name,
            String email,
            String password,
            String phoneNumber,
            Gender gender,
            Address address) {
        Assert.hasText(name, "이름은 필수 값입니다");
        Assert.hasText(email, "이메일 주소는 필수 값입니다");
        Assert.hasText(password, "비밀번호는 필수 값입니다");
        Assert.hasText(phoneNumber, "전화번호는 필수 값입니다");
        Assert.notNull(gender, "성별은 필수 값입니다");
        Assert.notNull(address, "주소는 필수 값입니다");
    }

}
