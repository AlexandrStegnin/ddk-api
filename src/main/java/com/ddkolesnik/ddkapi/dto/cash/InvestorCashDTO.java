package com.ddkolesnik.ddkapi.dto.cash;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO для создания проводок из 1С
 *
 * @author Alexandr Stegnin
 */

@Data
@Schema(name = "InvestorCash", description = "Информация о вложениях инвестора из 1С")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvestorCashDTO {

    @NotBlank(message = "Код инвестора должен быть указан")
    @Schema(implementation = String.class, name = "investorCode", description = "Код инвестора")
    String investorCode;

    @NotBlank(message = "Объект должен быть указан")
    @Schema(implementation = String.class, name = "facility", description = "Объект вложения")
    String facility;

    @NotNull(message = "Сумма должна быть указана")
    @Schema(implementation = BigDecimal.class, name = "givenCash", description = "Сумма вложения")
    BigDecimal givenCash;

    @NotNull(message = "Дата вложения должна быть указана")
    @Schema(implementation = LocalDate.class, name = "dateGiven", description = "Дата вложения")
    LocalDate dateGiven;

    @NotBlank(message = "Источник должен быть указан")
    @Schema(implementation = String.class, name = "cashSource", description = "Источник вложений (счёт или название)")
    String cashSource;

    @NotBlank(message = "Идентификатор транзакции должен быть указан")
    @Schema(implementation = String.class, name = "transactionUUID", description = "Идентификатор транзакции из 1С")
    String transactionUUID;

    @Schema(implementation = Boolean.class, name = "delete", defaultValue = "false", description = "Флаг о необходимости удаления транзакции")
    boolean delete = false;

    @Schema(implementation = String.class, name = "accountingCode", description = "Код бухгалтерского учёта (статьи)", example = "НФ-000001")
    String accountingCode;

    @Schema(implementation = String.class, name = "investorSellerCode", description = "Код инвестора, продающего долю")
    String investorSellerCode;

}
