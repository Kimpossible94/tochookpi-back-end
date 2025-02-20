package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.dto.UserAuthDTO;
import com.tochookpi.tochookpi.dto.UserDTO;
import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.exception.ApiErrorResponses;
import com.tochookpi.tochookpi.exception.ErrorResponse;
import com.tochookpi.tochookpi.exception.TochookpiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/temp")
public class TempController {

    @GetMapping("null-error")
    public ResponseEntity<String> nullError() {
        if(true) {
            throw new NullPointerException("Null Error");
        }
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
    }

    @GetMapping("custom-error")
    public ResponseEntity<String> customError() {
        if(true) {
            throw new TochookpiException(ErrorCode.DUPLICATE_USER);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원가입")
    @ApiErrorResponses({ErrorCode.DUPLICATE_USER, ErrorCode.INVALID_EMAIL_FORMAT, ErrorCode.INVALID_PASSWORD_FORMAT})
    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserAuthDTO userAuthDTO) {
        UserDTO userDTO = new UserDTO();
        return ResponseEntity.ok(userDTO);
    }
}
