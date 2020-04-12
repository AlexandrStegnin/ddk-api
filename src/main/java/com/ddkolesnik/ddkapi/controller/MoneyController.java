package com.ddkolesnik.ddkapi.controller;

import com.ddkolesnik.ddkapi.configuration.ApiErrorResponse;
import com.ddkolesnik.ddkapi.dto.MoneyDTO;
import com.ddkolesnik.ddkapi.exception.ApiException;
import com.ddkolesnik.ddkapi.service.AppKeyService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.ddkolesnik.ddkapi.util.Constant.PATH_MONIES;

/**
 * @author Alexandr Stegnin
 */

@Validated
@RestController
@RequestMapping(PATH_MONIES)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "Money", description = "API для получения информации о деньгах инвесторов")
public class MoneyController {

    MoneyService moneyService;

    AppKeyService appKeyService;

    @Operation(summary = "Получить список денег инвестора по параметрам", tags = {"Money"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorResponse.class))))})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<MoneyDTO> getAllInvestorMonies(@Parameter(description = "Ключ приложения.", schema = @Schema(implementation = String.class))
                                               @PathVariable(name = "appKey") String appKey,
                                               @Parameter(description = "Фильтр", schema = @Schema(implementation = MoneyFilter.class))
                                               @Valid @RequestBody MoneyFilter filter) {
        if (!appKeyService.existByKey(appKey)) {
            throw new ApiException("Доступ запрещён", HttpStatus.FORBIDDEN);
        }
        return moneyService.findAllDTO(filter);
    }
}
