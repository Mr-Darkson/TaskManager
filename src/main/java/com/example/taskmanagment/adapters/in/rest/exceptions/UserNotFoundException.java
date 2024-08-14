package com.example.taskmanagment.adapters.in.rest.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserNotFoundException extends RuntimeException {
    private final String defaultText = "User not found";
    public UserNotFoundException(String message) {
        super(message);
    }
}
