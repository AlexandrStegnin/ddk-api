package com.ddkolesnik.ddkapi.specification.filter;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Alexandr Stegnin
 */

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MoneyFilter extends AbstractFilter {

    String facility;

    String investor;

    String partnerCode;

    public String getPartnerCode() {
        return Objects.isNull(partnerCode) ? "" : "investor" + partnerCode;
    }

    public MoneyFilter(LocalDate fromDate, LocalDate toDate, String facility, String investor, String partnerCode) {
        super(fromDate, toDate);
        this.facility = facility;
        this.investor = investor;
        this.partnerCode = partnerCode;
    }
}
