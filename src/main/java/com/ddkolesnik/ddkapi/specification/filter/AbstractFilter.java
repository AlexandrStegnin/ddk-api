package com.ddkolesnik.ddkapi.specification.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Alexandr Stegnin
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractFilter {

    LocalDate fromDate;

    LocalDate toDate;

}
