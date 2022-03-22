package com.ddkolesnik.ddkapi.dto.cash;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author Alexandr Stegnin
 */
@Data
@Schema(name = "DeleteCash")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteCashDTO {

  @NotBlank(message = "Идентификатор должен быть указан")
  @Schema(implementation = String.class, name = "transactionUUID", description = "Идентификатор")
  String transactionUUID;

}
