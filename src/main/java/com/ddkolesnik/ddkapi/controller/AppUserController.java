package com.ddkolesnik.ddkapi.controller;

import com.ddkolesnik.ddkapi.configuration.exception.ApiErrorResponse;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ddkolesnik.ddkapi.util.Constant.*;

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
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorResponse.class))))})
    @PostMapping(path = UPDATE_USER, consumes = "application/x-www-form-urlencoded;charset=UTF-8",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AppUserDTO update(@Parameter(description = "Ключ приложения.", schema = @Schema(implementation = String.class))
                             @PathVariable(name = "token") @ValidToken String token,
                             @Parameter(description = "Пользователь", schema = @Schema(implementation = AppUserDTO.class))
                             @Valid AppUserDTO appUser) {
        log.info("POST with application/x-www-form-urlencoded;charset=UTF-8. USER = {}", appUser);
        return appUserService.update(appUser);
    }

    @Operation(summary = "Добавить/изменить пользователя", tags = {"AppUser"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorResponse.class))))})
    @PostMapping(path = UPDATE_USER_JSON, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AppUserDTO updateUser(@Parameter(description = "Ключ приложения.", schema = @Schema(implementation = String.class))
                                 @PathVariable(name = "token") @ValidToken String token,
                                 @Parameter(description = "Пользователь", schema = @Schema(implementation = AppUserDTO.class))
                                 @Valid @RequestBody AppUserDTO appUser) {
        log.info("POST with application/json. USER = {}", appUser);
        return appUserService.update(appUser);
    }

    @Operation(summary = "Добавить/изменить пользователя", tags = {"AppUser"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorResponse.class))))})
    @GetMapping(path = UPDATE_USER,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AppUserDTO saveUser(@Parameter(description = "Ключ приложения.", schema = @Schema(implementation = String.class))
                               @PathVariable(name = "token") @ValidToken String token,
                               @Parameter(description = "Код инвестора", schema = @Schema(implementation = String.class))
                               @RequestParam(name = "partnerCode") String partnerCode,
                               @Parameter(description = "Фамилия инвестора", schema = @Schema(implementation = String.class))
                               @RequestParam(name = "lastName") String lastName,
                               @Parameter(description = "Email инвестора", schema = @Schema(implementation = String.class))
                               @RequestParam(name = "email", required = false) String email) {
        log.info("GET with application/json. Parameters = [partnerCode = " + partnerCode + ", lastName = " + lastName + ", email = " + email + "]");
        return appUserService.update(partnerCode, lastName, email);
    }

}
