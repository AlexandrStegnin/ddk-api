package com.ddkolesnik.ddkapi.controller;

import com.ddkolesnik.ddkapi.configuration.ApiErrorResponse;
import com.ddkolesnik.ddkapi.configuration.annotation.ValidToken;
import com.ddkolesnik.ddkapi.dto.app.AppUserDTO;
import com.ddkolesnik.ddkapi.service.AppUserService;
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
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ddkolesnik.ddkapi.util.Constant.UPDATE_USER;
import static com.ddkolesnik.ddkapi.util.Constant.USERS;

/**
 * @author Alexandr Stegnin
 */

@SuppressWarnings("unused")
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
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorResponse.class))))})
    @PutMapping(path = UPDATE_USER, consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AppUserDTO update(@Parameter(description = "Ключ приложения.", schema = @Schema(implementation = String.class))
                             @PathVariable(name = "token") @ValidToken String token,
                             @Parameter(description = "Пользователь", schema = @Schema(implementation = AppUserDTO.class))
                             @Valid @RequestBody AppUserDTO appUser) {
        return appUserService.update(appUser);
    }

}
