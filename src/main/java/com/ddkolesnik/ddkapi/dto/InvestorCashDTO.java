package com.ddkolesnik.ddkapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO для создания проводок из 1С
 *
 * @author Alexandr Stegnin
 */

@Data
@Schema(name = "InvestorCash", implementation = InvestorCashDTO.class, description = "Информация о вложениях инвестора из 1С")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvestorCashDTO {

    @Schema(implementation = String.class, name = "investorCode", description = "Код инвестора")
    String investorCode;

    @Schema(implementation = String.class, name = "facility", description = "Объект вложения")
    String facility;

    @Schema(implementation = BigDecimal.class, name = "givenCash", description = "Сумма вложения")
    BigDecimal givenCash;

    @Schema(implementation = LocalDate.class, name = "dateGiven", description = "Дата вложения")
    LocalDate dateGiven;

    @Schema(implementation = String.class, name = "cashSource", description = "Источник вложений (БИК Банка)")
    String cashSource;

    @Schema(implementation = String.class, name = "transactionUUID", description = "Идентификатор транзакции из 1С")
    String transactionUUID;

}
