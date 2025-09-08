package com.tochookpi.tochookpi.enums;

public enum FileType {
    IMAGE("image"),
    VIDEO("video");

    private final String value;
    FileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
