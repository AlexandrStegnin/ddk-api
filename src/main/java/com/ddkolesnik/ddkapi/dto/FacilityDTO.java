package com.ddkolesnik.ddkapi.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author Alexandr Stegnin
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FacilityDTO {

    String name;

}
