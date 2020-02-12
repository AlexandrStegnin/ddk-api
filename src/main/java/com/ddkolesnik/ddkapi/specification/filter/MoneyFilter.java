package com.ddkolesnik.ddkapi.specification.filter;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

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
}
