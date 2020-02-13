package com.ddkolesnik.ddkapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author Alexandr Stegnin
 */

@Data
@Schema(name = "Facility", implementation = FacilityDTO.class, description = "Информация об объекте")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FacilityDTO {

    @Schema(implementation = String.class, name = "Name", description = "Название объекта")
    String name;

}
