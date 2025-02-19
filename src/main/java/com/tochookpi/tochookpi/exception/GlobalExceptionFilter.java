package com.tochookpi.tochookpi.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tochookpi.tochookpi.enums.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GlobalExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    public GlobalExceptionFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (TochookpiException e) {
            setErrorResponse(response, e.getErrorCode(), request.getRequestURI());
        } catch (Exception e) {
            setErrorResponse(response, ErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI());
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode, String path) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getHttpStatus(),
                errorCode.getCode(),
                errorCode.getMessage(),
                path
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
