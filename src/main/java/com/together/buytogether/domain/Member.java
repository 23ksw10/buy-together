package com.together.buytogether.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Entity
@EqualsAndHashCode(of = "id")
@Getter
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


}
