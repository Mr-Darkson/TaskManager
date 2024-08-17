package com.example.taskmanagment.adapters.in.rest.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Никитин Арсений
 * Исключение выбрасывается, если у пользователя нет прав на использование определённых функций
 */
@Getter
@NoArgsConstructor
public class PermissionDeniedException extends RuntimeException {
    private final String defaultText = "You do not have authority to perform this action";
    public PermissionDeniedException(String message) {
        super(message);
    }
}
