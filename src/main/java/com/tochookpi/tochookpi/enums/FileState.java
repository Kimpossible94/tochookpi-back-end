package com.tochookpi.tochookpi.enums;

import lombok.Getter;

@Getter
public enum FileState {
    DELETE("delete"),
    NEW("new");

    private final String value;
    FileState(String value) {
        this.value = value;
    }
}
