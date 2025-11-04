package com.tochookpi.tochookpi.enums;

import lombok.Getter;

@Getter
public enum FileType {
    IMAGE("image"),
    VIDEO("video");

    private final String value;
    FileType(String value) {
        this.value = value;
    }
}
