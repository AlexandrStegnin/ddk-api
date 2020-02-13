package com.ddkolesnik.ddkapi.dto.bitrix;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author Alexandr Stegnin
 */

@Data
@Schema(name = "Email", implementation = BitrixContactDTO.class, description = "Адреса электронной почты из битрикс системы")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BitrixEmailDTO {

    @Schema(implementation = String.class, name = "type", description = "Тип адреса электронной почты (HOME/WORK)")
    String type;

    @Schema(implementation = String.class, name = "email", description = "Адрес электронной почты")
    String email;

}
