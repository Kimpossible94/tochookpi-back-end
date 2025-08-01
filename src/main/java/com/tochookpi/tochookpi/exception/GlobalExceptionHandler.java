package com.tochookpi.tochookpi.exception;

import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.service.storage.OrphanFileLogService;
import com.tochookpi.tochookpi.service.storage.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final OrphanFileLogService orphanFileLogService;

    @ExceptionHandler(S3OrphanFileException.class)
    public ResponseEntity<ErrorResponse> handleS3OrphanFileException(S3OrphanFileException ex, HttpServletRequest request) {
        if(ex.getImageUrls() != null) {
            for (String url : ex.getImageUrls()) {
                orphanFileLogService.saveLog(url, "고아 객체 생성");
            }
        }

        return handleCustomException(ex, request);
    }

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
        e.printStackTrace();
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
