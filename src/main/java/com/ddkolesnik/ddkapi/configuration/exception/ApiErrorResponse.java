package com.ddkolesnik.ddkapi.configuration.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * @author Alexandr Stegnin
 */

@Getter
@Schema(name = "ApiErrorResponse", description = "Информация об ошибке при выполнении запроса")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ApiErrorResponse {

    @Schema(implementation = HttpStatus.class, name = "status", description = "Статус выполнения запроса")
    HttpStatus status;

    @Schema(implementation = String.class, name = "message", description = "Сообщение")
    String message;

    @Schema(implementation = Instant.class, name = "timestamp", description = "Время")
    Instant timestamp;

    public ApiErrorResponse(HttpStatus status, String message, Instant timestamp) {
        this.status= status;
        this.message = message;
        this.timestamp = timestamp;
    }

}
