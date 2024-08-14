package com.example.taskmanagment.adapters.in.rest.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PermissionDeniedException extends RuntimeException {
    private final String defaultText = "You do not have authority to perform this action";
    public PermissionDeniedException(String message) {
        super(message);
    }
}
