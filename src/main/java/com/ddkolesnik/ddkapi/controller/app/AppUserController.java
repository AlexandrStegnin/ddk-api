package com.ddkolesnik.ddkapi.controller.app;

import com.ddkolesnik.ddkapi.configuration.annotation.ValidToken;
import com.ddkolesnik.ddkapi.configuration.exception.ApiErrorResponse;
import com.ddkolesnik.ddkapi.configuration.exception.ApiSuccessResponse;
import com.ddkolesnik.ddkapi.dto.app.AppUserDTO;
import com.ddkolesnik.ddkapi.service.app.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.ddkolesnik.ddkapi.util.Constant.UPDATE_USER;
import static com.ddkolesnik.ddkapi.util.Constant.USERS;

/**
 * @author Alexandr Stegnin
 */

@SuppressWarnings("unused")
@Slf4j
@Validated
@RestController
@RequestMapping(USERS)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "AppUser", description = "API для обновления информации о пользователях системы")
public class AppUserController {

    AppUserService appUserService;

    @Operation(summary = "Добавить/изменить пользователя", tags = {"AppUser"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiSuccessResponse.class)))),
            @ApiResponse(responseCode = "Error", description = "Произошла ошибка",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorResponse.class))))})
    @PostMapping(path = UPDATE_USER, consumes = "application/x-www-form-urlencoded;charset=UTF-8",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ApiSuccessResponse update(@Parameter(description = "Ключ приложения.", schema = @Schema(implementation = String.class))
                             @PathVariable(name = "token") @ValidToken String token,
                             @Parameter(description = "Пользователь", schema = @Schema(implementation = AppUserDTO.class))
                             @Valid AppUserDTO appUser) {
        appUserService.update(appUser);
        log.info("Пользователь успешно обновлён [{}]", appUser);
        return new ApiSuccessResponse(HttpStatus.OK, "Пользователь успешно сохранён");
    }

}
