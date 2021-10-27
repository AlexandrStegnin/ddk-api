package com.ddkolesnik.ddkapi.configuration.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

/**
 * @author Alexandr Stegnin
 */
@Getter
@RequiredArgsConstructor
@Schema(name = "ApiSuccessResponse", description = "Информация об успешном выполнении запроса")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ApiSuccessResponse {

    @Schema(implementation = HttpStatus.class, name = "status", description = "Статус выполнения запроса")
    HttpStatus status;

    @Schema(implementation = String.class, name = "message", description = "Сообщение")
    String message;

}
