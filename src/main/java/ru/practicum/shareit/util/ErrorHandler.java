package ru.practicum.shareit.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.util.exception.ConditionsNotMetException;
import ru.practicum.shareit.util.exception.DataConflictException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.UnauthorizedException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.warn("Ошибка валидации: {}", errors);
        return errors;
    }

    @ExceptionHandler(ConditionsNotMetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConditionsNotMetException(ConditionsNotMetException ex) {
        log.warn("Ошибка условий: {}", ex.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException ex) {
        log.warn("Не найдено: {}", ex.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(DataConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDataConflictException(DataConflictException ex) {
        log.warn("Конфликт данных: {}", ex.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("Доступ запрещен: {}", ex.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleOtherExceptions(Exception ex) {
        log.error("Неожиданная ошибка", ex);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Произошла непредвиденная ошибка.");
        errorResponse.put("details", ex.getMessage());
        return errorResponse;
    }
}
