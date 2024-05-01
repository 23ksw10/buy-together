package com.together.buytogether.common.execption;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<ErrorResponse> handleNotExistedUserException(CustomException ex) {
        return ErrorResponse.toResponseEntity(ex.getErrorCode());
    }
}
