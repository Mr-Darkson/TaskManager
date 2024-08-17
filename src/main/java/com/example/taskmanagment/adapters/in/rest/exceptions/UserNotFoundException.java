package com.example.taskmanagment.adapters.in.rest.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Никитин Арсений
 * Исключение выбрасывается если пользователь не был найден по определённым данным
 */
@Getter
@NoArgsConstructor
public class UserNotFoundException extends RuntimeException {
    private final String defaultText = "User not found";
    public UserNotFoundException(String message) {
        super(message);
    }
}
