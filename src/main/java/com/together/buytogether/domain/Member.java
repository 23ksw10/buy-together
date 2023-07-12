package com.together.buytogether.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(of = "id")
public class Member {
    @Id
    private Long id;

    private Address address;

    private String name;
    private String phoneNumber;

    
}
