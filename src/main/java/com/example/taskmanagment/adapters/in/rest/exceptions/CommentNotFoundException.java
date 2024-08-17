package com.example.taskmanagment.adapters.in.rest.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Никитин Арсений
 * Исключение выбрасывается если комментарий не был найден по определённым данным
 */

@Getter
@NoArgsConstructor
public class CommentNotFoundException extends RuntimeException {
    private final String defaultText = "Comment not found";
    public CommentNotFoundException(String message) {
        super(message);
    }
}
