package com.ddkolesnik.ddkapi.specification.filter;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(implementation = LocalDate.class, name = "fromDate", description = "Дата вложений С")
    LocalDate fromDate;

    @Schema(implementation = LocalDate.class, name = "toDate", description = "Дата вложений ПО")
    LocalDate toDate;

    @Schema(implementation = Integer.class, name = "limit", description = "Количество записей")
    int limit = 50;

    @Schema(implementation = Integer.class, name = "offset", description = "С какой записи запрашивать")
    int offset = 0;

}
