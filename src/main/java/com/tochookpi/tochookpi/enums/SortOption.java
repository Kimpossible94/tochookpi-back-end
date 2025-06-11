package com.tochookpi.tochookpi.enums;

public enum SortOption {
    LATEST("latest"),
    POPULAR("popular");

    private final String value;
    SortOption(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SortOption fromValue(String value) {
        for (SortOption sort : values()) {
            if (sort.getValue().equalsIgnoreCase(value)) {
                return sort;
            }
        }
        return LATEST;
    }
}
