package com.ddkolesnik.ddkapi.dto.money;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

/**
 * @author Alexandr Stegnin
 */

@Data
@Schema(name = "Facility", description = "Информация об объекте")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FacilityDTO {

    @NotBlank(message = "Название должно быть указано")
    @Schema(implementation = String.class, name = "name", description = "Краткое название объекта", required = true)
    String name;

    @Schema(implementation = String.class, name = "projectUUID", description = "Идентификатор объекта из 1С")
    String projectUUID;

}
