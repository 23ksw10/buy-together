package com.together.buytogether.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@EqualsAndHashCode(of = "id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    private Address address;

    private String detailAddress;

    private String name;
    private String phoneNumber;

    @Column(unique = true)
    private String loginId;

    private String password;

    private String sex;

    @Builder
    public Member(String name, String phoneNumber,String loginId, String password, String sex){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.loginId = loginId;
        this.password = password;
        this.sex = sex;
    }

}
