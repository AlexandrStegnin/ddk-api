package com.ddkolesnik.ddkapi.configuration.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * @author Alexandr Stegnin
 */

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ApiErrorResponse {

    HttpStatus status;
    String message;
    Instant timestamp;

    public ApiErrorResponse(HttpStatus status, String message, Instant timestamp) {
        this.status= status;
        this.message = message;
        this.timestamp = timestamp;
    }

}
