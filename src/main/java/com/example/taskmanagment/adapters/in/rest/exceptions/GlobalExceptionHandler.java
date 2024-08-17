package com.example.taskmanagment.adapters.in.rest.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Никитин Арсений
 * Глобальный обработчик ошибок, созданный для обработки иключений, выбрашиваемых в контроллерах
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication Failed: " + ex.getMessage());
    }

    /**
     * Обрабатывает исключение "Отсутствую права доступа"
     * @param ex PermissionDeniedException
     * @return String
     */
    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<String> handlePermissionDeniedException(PermissionDeniedException ex) {
        String mess = ex.getMessage() == null ? ex.getDefaultText() : ex.getMessage();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied: " + mess);
    }
    /**
     * Обрабатывает исключение "Ошибка валидации"
     * @param ex MethodArgumentNotValidException
     * @return String
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    /**
     * Обрабатывает исключение "Передан недопустимый аргумент"
     * @param ex IllegalArgumentException
     * @return String
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data parsing error: " + ex.getMessage());
    }

    /**
     * Обрабатывает исключение "Несоответствие типа аргумента метода"
     * @param ex MethodArgumentTypeMismatchException
     * @return String
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data parsing error: " + ex.getMessage());
    }

    /**
     * Обрабатывает исключение "Пользователь не найден"
     * @param ex UserNotFoundException
     * @return String
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        String mess = ex.getMessage() == null ? ex.getDefaultText() : ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An error occurred with user: " + mess);
    }
    /**
     * Обрабатывает исключение "Задача не найдена"
     * @param ex TaskNotFoundException
     * @return String
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> handleTaskNotFoundException(TaskNotFoundException ex) {
        String mess = ex.getMessage() == null ? ex.getDefaultText() : ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An error occurred with task: " + mess);
    }
    /**
     * Обрабатывает исключение "Комментарий не найден"
     * @param ex CommentNotFoundException
     * @return String
     */
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handleCommentNotFoundException(CommentNotFoundException ex) {
        String mess = ex.getMessage() == null ? ex.getDefaultText() : ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An error occurred with comment: " + mess);
    }

    /**
     * Обрабатывает неопознанные исключения
     * @param ex Exception
     * @return String
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
    }

}
