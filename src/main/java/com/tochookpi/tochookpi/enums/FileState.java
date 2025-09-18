package com.tochookpi.tochookpi.enums;

public enum FileState {
    DELETE("delete"),
    NEW("new");

    private final String value;
    FileState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
