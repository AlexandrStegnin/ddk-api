package com.ddkolesnik.ddkapi.controller;

import com.ddkolesnik.ddkapi.configuration.annotation.ValidToken;
import com.ddkolesnik.ddkapi.configuration.exception.ApiErrorResponse;
import com.ddkolesnik.ddkapi.service.BitrixContactService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

import static com.ddkolesnik.ddkapi.util.Constant.*;

/**
 * Контроллер для работы с контактами из Битрикс 24
 *
 * @author Alexandr Stegnin
 */

@SuppressWarnings("unused")
@Slf4j
@Validated
@RestController
@RequestMapping(BITRIX_CONTACT)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "Bitrix Contacts Update", description = "API для взаимодействия с системой Битрикс 24")
public class BitrixContactController {

    BitrixContactService bitrixContactService;

    @Operation(summary = "Принудительное обновление информации из Битрикс 24", tags = {"Bitrix Contacts Update"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorResponse.class))))})
    @GetMapping(BITRIX_CONTACTS_MERGE)
    public HttpStatus updateContacts(@Parameter(description = "Ключ приложения.", schema = @Schema(implementation = String.class))
                                 @PathVariable(name = "token") @ValidToken String token) {
        WebClient client = WebClient
                .builder()
                .baseUrl(BITRIX_CONTACT_UPDATE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        ClientResponse response = client.get().exchange()
                .doOnSuccess(clientResponse -> {
                    bitrixContactService.mergeContacts();
                    log.info("Синхронизация контактов завершена.");
                })
                .doOnError(resp -> log.error("Ошибка: " + resp.getMessage()))
                .block();
        if (Objects.nonNull(response)) {
            return response.statusCode();
        }
        return HttpStatus.BAD_REQUEST;
    }

}