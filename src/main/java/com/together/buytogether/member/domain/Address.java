package com.together.buytogether.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.util.Assert;

@Embeddable
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Address {
    @Column(name = "address", nullable = false)
    @Comment("도로명 주소")
    private String address;
    @Column(name = "detail_address", nullable = false)
    @Comment("상세 주소")
    private String detailAddress;

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
