package com.ddkolesnik.ddkapi.configuration.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Alexandr Stegnin
 */

@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
