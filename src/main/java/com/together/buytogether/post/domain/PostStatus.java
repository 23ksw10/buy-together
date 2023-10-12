package com.together.buytogether.post.domain;

public enum PostStatus {
    OPEN("OPEN"),
    CLOSED("CLOSED");

    private final String description;

    PostStatus(String description) {
        this.description = description;
    }
}
