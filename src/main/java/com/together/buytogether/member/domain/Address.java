package com.together.buytogether.member.domain;

import org.springframework.util.Assert;

public class Address {
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
