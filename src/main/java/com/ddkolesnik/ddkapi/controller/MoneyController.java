package com.ddkolesnik.ddkapi.controller;

import com.ddkolesnik.ddkapi.dto.MoneyDTO;
import com.ddkolesnik.ddkapi.service.MoneyService;
import com.ddkolesnik.ddkapi.specification.filter.MoneyFilter;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static com.ddkolesnik.ddkapi.util.Constant.PATH_MONIES;

/**
 * @author Alexandr Stegnin
 */

@RestController
@RequestMapping(PATH_MONIES)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "Money", description = "API для получения информации о деньгах инвесторов")
public class MoneyController {

    MoneyService moneyService;

    @Operation(summary = "Получить список денег инвестора по параметрам", tags = {"Money"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))})
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<MoneyDTO> getMonies(
            @Parameter(description = "Код партнёра.", example = "017", schema = @Schema(implementation = String.class))
            @RequestParam String partnerCode,
            @Parameter(description = "Логин инвестора.", example = "investor017", schema = @Schema(implementation = String.class))
            @RequestParam(required = false) String login,
            @Parameter(description = "Объект вложений.", example = "Чаплина", schema = @Schema(implementation = String.class))
            @RequestParam(required = false) String facility,
            @Parameter(description = "Вложения С даты.", example = "2016-01-01", schema = @Schema(implementation = LocalDate.class))
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Вложения ПО дату.", example = "2020-01-01", schema = @Schema(implementation = LocalDate.class))
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        MoneyFilter filter = new MoneyFilter(fromDate, toDate, facility, login, partnerCode);
        return moneyService.findAllDTO(filter);
    }

}
