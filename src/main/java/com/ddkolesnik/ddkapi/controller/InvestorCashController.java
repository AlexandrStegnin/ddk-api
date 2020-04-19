package com.ddkolesnik.ddkapi.controller;

import com.ddkolesnik.ddkapi.configuration.annotation.ValidToken;
import com.ddkolesnik.ddkapi.configuration.exception.ApiErrorResponse;
import com.ddkolesnik.ddkapi.dto.InvestorCashDTO;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.ddkolesnik.ddkapi.service.InvestorCashService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ddkolesnik.ddkapi.util.Constant.PATH_INVESTOR_CASH;
import static com.ddkolesnik.ddkapi.util.Constant.PATH_INVESTOR_CASH_CREATE;

/**
 * Контроллер для работы с проводками из 1С
 *
 * @author Alexandr Stegnin
 */


@SuppressWarnings("unused")
@Slf4j
@Validated
@RestController
@RequestMapping(PATH_INVESTOR_CASH)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "Create/Update investor cash", description = "API для взаимодействия с системой 1C")
public class InvestorCashController {

    InvestorCashService investorCashService;

    @Operation(summary = "Создание проводки на основании данных из 1С", tags = {"Create/Update investor cash"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorResponse.class))))})
    @PostMapping(PATH_INVESTOR_CASH_CREATE)
    public Money createInvestorCash(@Parameter(description = "Ключ приложения.", schema = @Schema(implementation = String.class))
                                         @PathVariable(name = "token") @ValidToken String token,
                                    @Parameter(description = "Проводка из 1С", schema = @Schema(implementation = InvestorCashDTO.class))
                                         @Valid @RequestBody InvestorCashDTO dto) {
        return investorCashService.update(dto);
    }

}
