package com.example.taskmanagment.adapters.in.rest.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentNotFoundException extends RuntimeException {
    private final String defaultText = "Comment not found";
    public CommentNotFoundException(String message) {
        super(message);
    }
}
