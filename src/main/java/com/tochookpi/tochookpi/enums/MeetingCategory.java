package com.tochookpi.tochookpi.enums;

public enum MeetingCategory {
    REGULAR("정기모임"),
    MEAL("식사모임"),
    DRINK("술모임"),
    EXERCISE("운동모임"),
    HOBBY("취미모임"),
    ETC("식사모임");

    private final String value;

    MeetingCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MeetingCategory fromValue(String value) {
        for (MeetingCategory category : values()) {
            if (category.getValue().equalsIgnoreCase(value)) {
                return category;
            }
        }
        return ETC;
    }
}