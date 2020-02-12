package com.ddkolesnik.ddkapi.dto.bitrix;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BitrixContactDTO {

    String name;

    String secondName;

    String lastName;

    LocalDate birthday;

    String code;

    Set<BitrixEmailDTO> emails;

}
