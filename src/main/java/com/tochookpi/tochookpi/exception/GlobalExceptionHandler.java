package com.tochookpi.tochookpi.exception;

import com.tochookpi.tochookpi.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TochookpiException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(TochookpiException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getHttpStatus(),
                errorCode.getCode(),
                errorCode.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }
}
