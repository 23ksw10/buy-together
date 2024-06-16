package com.together.buytogether.common.error;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String customMessage;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.customMessage = errorCode.getMessage();
    }

    public CustomException(ErrorCode errorCode, String customMessage) {
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    @Override
    public String getMessage() {
        return customMessage;
    }

}
