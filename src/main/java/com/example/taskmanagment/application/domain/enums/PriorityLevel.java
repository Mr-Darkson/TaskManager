package com.example.taskmanagment.application.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PriorityLevel {
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW");

    private final String displayName;

    @JsonValue
    public String getName() {
        return displayName;
    }

    @JsonCreator
    public static PriorityLevel forValue(String value) {
        for (PriorityLevel level : values()) {
            if (level.displayName.equals(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException(String.format("Unknown value '%s'", value));
    }


}
