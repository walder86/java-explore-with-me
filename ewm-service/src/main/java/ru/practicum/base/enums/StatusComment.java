package ru.practicum.base.enums;

import lombok.Getter;

@Getter
public enum StatusComment {
    CONFIRMED, REJECTED, PENDING, CANCELED;

    public static StatusComment from(String status) {
        for (StatusComment value: StatusComment.values()) {
            if (value.name().equalsIgnoreCase(status)) {
                return value;
            }
        }
        return null;
    }
}
