package com.ddkolesnik.ddkapi.dto.cash;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * DTO для хранения информации о том, с кем заключён договор (ЮЛ/ФЛ)
 *
 * @author Alexandr Stegnin
 */

@Data
@Schema(name = "UserAgreement", description = "Информация с кем заключён договор")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAgreementDTO {

    @NotBlank(message = "Название объекта должно быть указано")
    @Schema(implementation = String.class, name = "facility", description = "Название объекта")
    String facility;

    @NotBlank(message = "С кем заключён договор должно быть заполнено")
    @Schema(implementation = String.class, name = "concludedWith", description = "С кем заключён договор")
    String concludedWith;

    @NotBlank(message = "От кого заключён договор должно быть заполнено")
    @Schema(implementation = String.class, name = "concludedFrom", description = "От кого заключён договор")
    String concludedFrom;

    @NotNull(message = "Налоговая ставка должна быть заполнена")
    @Schema(implementation = Double.class, name = "taxRate", description = "Налоговая ставка")
    Double taxRate;

//    @NotBlank(message = "От кого заключён договор (организация) должно быть заполнено")
    @Schema(implementation = String.class, name = "organization", description = "От кого заключён договор (организация)")
    String organization;

}
