package com.ddkolesnik.ddkapi.controller.money;

import com.ddkolesnik.ddkapi.configuration.exception.ApiErrorResponse;
import com.ddkolesnik.ddkapi.configuration.annotation.ValidToken;
import com.ddkolesnik.ddkapi.dto.money.MoneyDTO;
import com.ddkolesnik.ddkapi.service.money.MoneyService;
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
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.ddkolesnik.ddkapi.util.Constant.PATH_MONIES;

/**
 * @author Alexandr Stegnin
 */

@SuppressWarnings("unused")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(PATH_MONIES)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "Money", description = "API для получения информации о деньгах инвесторов")
public class MoneyController {

    MoneyService moneyService;

    @Operation(summary = "Получить список денег инвестора по параметрам", tags = {"Money"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorResponse.class))))})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<MoneyDTO> getAllInvestorMonies(@Parameter(description = "Ключ приложения.", schema = @Schema(implementation = String.class))
                                               @PathVariable(name = "token") @ValidToken String token,
                                               @Parameter(description = "Фильтр", schema = @Schema(implementation = MoneyFilter.class))
                                               @Valid @RequestBody MoneyFilter filter) {
        return moneyService.findAllDTO(filter);
    }
}
