package com.tochookpi.tochookpi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private HttpStatus status;
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponse(HttpStatus status, String code, String message, String path) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }
}