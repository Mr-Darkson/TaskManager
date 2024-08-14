package com.example.taskmanagment.application.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TaskStatus {
    WAITING("WAITING"),
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED");

    private final String displayName;

    @JsonValue
    public String getName() {
        return displayName;
    }

    @JsonCreator
    public static TaskStatus forValue(String value) {
        for (TaskStatus status : values()) {
            if (status.displayName.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException(String.format("Unknown value '%s'", value));
    }

}
