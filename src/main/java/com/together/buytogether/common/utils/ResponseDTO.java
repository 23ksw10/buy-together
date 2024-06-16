package com.together.buytogether.common.utils;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseDTO<T> {
    private final Status status;
    private final T data;
    private final String message;

    public static <T> ResponseDTO<T> successResult(T data) {
        return ResponseDTO.<T>builder()
                .status(Status.SUCCESS)
                .data(data)
                .message("Request Success")
                .build();
    }

    public static <T> ResponseDTO<T> failResult(T data) {
        return ResponseDTO.<T>builder()
                .status(Status.FAIL)
                .data(null)
                .message("Request Fail")
                .build();
    }

    private enum Status {
        SUCCESS,
        FAIL
    }
}
