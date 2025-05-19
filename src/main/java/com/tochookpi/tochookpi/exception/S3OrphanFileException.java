package com.tochookpi.tochookpi.exception;

import com.tochookpi.tochookpi.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class S3OrphanFileException extends TochookpiException {
    private final List<String> imageUrls;

    public S3OrphanFileException(ErrorCode errorCode, List<String> imageUrls) {
        super(errorCode);
        this.imageUrls = imageUrls;
    }

    public S3OrphanFileException(ErrorCode errorCode, String imageUrls) {
        super(errorCode);
        this.imageUrls = List.of(imageUrls);
    }

    public List<String> getImageUrls() { return imageUrls; }
}
