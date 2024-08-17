package com.example.taskmanagment.adapters.in.rest.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Никитин Арсений
 * Исключение выбрасывается если задача не была найдена по определённым данным
 */
@Getter
@NoArgsConstructor
public class TaskNotFoundException extends RuntimeException {

    private final String defaultText = "Task not found";

    public TaskNotFoundException(String message) {
        super(message);
    }
}
