package com.ddkolesnik.ddkapi.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import static com.ddkolesnik.ddkapi.util.Constant.UNKNOWN_FACILITY;
import static com.ddkolesnik.ddkapi.util.Constant.UNKNOWN_INVESTOR;

/**
 * @author Alexandr Stegnin
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MoneyDTO {

    InvestorDTO investor;

    FacilityDTO facility;

    BigDecimal givenCash;

    LocalDate dateGiven;

    public String getInvestor() {
        return Objects.isNull(investor) ? UNKNOWN_INVESTOR : investor.getLogin();
    }

    public String getFacility() {
        return Objects.isNull(facility) ? UNKNOWN_FACILITY : facility.getName();
    }

    public BigDecimal getGivenCash() {
        return givenCash.setScale(2, RoundingMode.HALF_UP);
    }

    public String getDateGiven() {
        return dateGiven.format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault()));
    }

}
