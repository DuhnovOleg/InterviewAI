package com.example.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleUserExists(UserAlreadyExistsException ex) {
        return Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "error", "USER_ALREADY_EXISTS",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleInvalidCredentials(InvalidCredentialsException ex) {
        return Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "error", "INVALID_CREDENTIALS",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleInvalidToken(InvalidTokenException ex) {
        return Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "error", "INVALID_TOKEN",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleOther(Exception ex) {
        return Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "error", "INTERNAL_SERVER_ERROR",
                "message", ex.getMessage()
        );
    }

}
