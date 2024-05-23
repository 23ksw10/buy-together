package com.together.buytogether.member.domain;

public class AddressFixture {

    private String address = "경기도 고양시 덕양구 화정로";

    private String detailAddress = "600동 0000호";

    public static AddressFixture aAddress() {
        return new AddressFixture();
    }

    public AddressFixture address(String address) {
        this.address = address;
        return this;
    }

    public AddressFixture detailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
        return this;
    }

    public Address build() {
        return new Address(address, detailAddress);
    }
}