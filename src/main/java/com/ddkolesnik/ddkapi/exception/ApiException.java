package com.ddkolesnik.ddkapi.exception;

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
