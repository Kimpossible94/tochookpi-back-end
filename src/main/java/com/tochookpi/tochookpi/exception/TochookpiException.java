package com.tochookpi.tochookpi.exception;

import com.tochookpi.tochookpi.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class TochookpiException extends RuntimeException {
    private final ErrorCode errorCode;


    public TochookpiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatusCode() {
        return errorCode.getHttpStatus();
    }

    public String getErrorCodeMessage() {
        return errorCode.getMessage();
    }
}
