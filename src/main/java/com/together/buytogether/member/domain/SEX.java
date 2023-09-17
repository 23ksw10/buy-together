package com.together.buytogether.member.domain;

public enum SEX {
    MALE("남자"),
    FEMALE("여자");

    private final String description;

    SEX(String description) {
        this.description = description;
    }
}
