package com.ddkolesnik.ddkapi.specification.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Alexandr Stegnin
 */

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MoneyFilter extends AbstractFilter {

    @Schema(implementation = String.class, name = "facility", description = "Объект вложений")
    String facility;

    @Schema(implementation = String.class, name = "investor", description = "Логин инвестора")
    String investor;

    @NotNull(message = "Код партнёра должен быть указан")
    @Schema(implementation = String.class, name = "partnerCode", description = "Код партнёра", required = true)
    String partnerCode;

    public String getPartnerCode() {
        return Objects.isNull(partnerCode) ? "" : "investor" + partnerCode;
    }

    public MoneyFilter(LocalDate fromDate, LocalDate toDate, String facility, String investor, String partnerCode,
                       int limit, int offset) {
        super(fromDate, toDate, limit, offset);
        this.facility = facility;
        this.investor = investor;
        this.partnerCode = partnerCode;
    }
}
