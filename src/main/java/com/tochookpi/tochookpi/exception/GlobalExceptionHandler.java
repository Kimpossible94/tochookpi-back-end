package com.tochookpi.tochookpi.exception;

import com.tochookpi.tochookpi.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TochookpiException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(TochookpiException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse errorResponse = setErrorResponse(request, errorCode);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    @ExceptionHandler({
            MissingPathVariableException.class, // @PathVariable
            MissingRequestCookieException.class, // @CookieValue
    })
    public ResponseEntity<ErrorResponse> handleMissingParams(Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = setErrorResponse(request, ErrorCode.MISSING_REQUIRED_PARAMETER);

        return ResponseEntity
                .status(ErrorCode.MISSING_REQUIRED_PARAMETER.getHttpStatus())
                .body(errorResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e, HttpServletRequest request) {
        ErrorResponse errorResponse = setErrorResponse(request, ErrorCode.INTERNAL_SERVER_ERROR);

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(errorResponse);
    }

    private ErrorResponse setErrorResponse(HttpServletRequest request, ErrorCode errorCode) {
        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getHttpStatus(),
                errorCode.getCode(),
                errorCode.getMessage(),
                request.getRequestURI()
        );

        return errorResponse;
    }
}
