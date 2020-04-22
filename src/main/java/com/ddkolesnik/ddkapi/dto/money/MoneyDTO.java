package com.ddkolesnik.ddkapi.dto.money;

import com.ddkolesnik.ddkapi.dto.bitrix.BitrixContactDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

import static com.ddkolesnik.ddkapi.util.Constant.UNKNOWN_FACILITY;
import static com.ddkolesnik.ddkapi.util.Constant.UNKNOWN_INVESTOR;

/**
 * @author Alexandr Stegnin
 */

@Data
@Schema(name = "Money", implementation = MoneyDTO.class, description = "Информация о вложениях инвестора")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MoneyDTO {

    InvestorDTO investor;

    FacilityDTO facility;

    @Schema(implementation = BigDecimal.class, name = "givenCash", description = "Сумма вложения")
    BigDecimal givenCash;

    @Schema(implementation = LocalDate.class, name = "dateGiven", description = "Дата вложения")
    LocalDate dateGiven;

    @Schema(implementation = BitrixContactDTO.class, name = "bitrixInfo", description = "Информация из битрикс системы")
    BitrixContactDTO bitrixInfo;

    public BigDecimal getGivenCash() {
        if (Objects.isNull(givenCash)) givenCash = BigDecimal.ZERO;
        return givenCash.setScale(2, RoundingMode.HALF_UP);
    }

    @Schema(implementation = String.class, name = "investor", description = "Логин инвестора")
    public String getInvestor() {
        return Objects.isNull(investor.getLogin()) ? UNKNOWN_INVESTOR : investor.getLogin();
    }

    @Schema(implementation = String.class, name = "facility", description = "Название объекта вложений")
    public String getFacility() {
        return Objects.isNull(facility.getName()) ? UNKNOWN_FACILITY : facility.getName();
    }
}
