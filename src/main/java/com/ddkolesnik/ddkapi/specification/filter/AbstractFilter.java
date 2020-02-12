package com.ddkolesnik.ddkapi.specification.filter;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author Alexandr Stegnin
 */

@Data
public abstract class AbstractFilter {

    LocalDate fromDate;

    LocalDate toDate;

}
