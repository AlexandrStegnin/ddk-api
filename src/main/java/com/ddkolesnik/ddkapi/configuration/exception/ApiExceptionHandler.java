package com.ddkolesnik.ddkapi.configuration.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ddkolesnik.ddkapi.util.Constant.INVALID_APP_TOKEN;

/**
 * @author Alexandr Stegnin
 */

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({ ApiException.class })
    protected ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(ex.getStatus(), ex.getMessage(), Instant.now()), ex.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handle(ConstraintViolationException exception) {
        String errorMessage = new ArrayList<>(exception.getConstraintViolations())
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining( "; "));
        HttpStatus status;
        if (errorMessage.equalsIgnoreCase(INVALID_APP_TOKEN)) {
            status = HttpStatus.FORBIDDEN;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(status, errorMessage, Instant.now());
        return new ResponseEntity<>(apiErrorResponse, status);
    }

    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handle(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, Objects.requireNonNull(FieldError::getDefaultMessage)))
                .entrySet()
                .stream()
                .map(entrySet -> entrySet.getKey() + ": "+ entrySet.getValue()).
                collect(Collectors.joining(",\n"));
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST, message, Instant.now());
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public void handle(RequestRejectedException e) {
        log.warn("Запрос отклонён: {}", e.getLocalizedMessage());
    }

    @ExceptionHandler
    public void handle(IllegalArgumentException e) {
        log.warn("Произошла ошибка: {}", e.getLocalizedMessage());
    }

    @ExceptionHandler
    public void handle(SQLSyntaxErrorException e) {
        log.warn("Ошибка базы данных: {}", e.getLocalizedMessage());
    }
}
