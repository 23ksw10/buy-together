package com.together.buytogether.member.domain;

public class AddressFixture {

    private String address = "address";

    private String detailAddress = "detailAddress";

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