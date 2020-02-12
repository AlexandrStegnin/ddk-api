package com.ddkolesnik.ddkapi.dto.bitrix;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author Alexandr Stegnin
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BitrixEmailDTO {

    String type;

    String email;

}
