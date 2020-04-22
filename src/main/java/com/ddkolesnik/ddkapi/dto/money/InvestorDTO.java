package com.ddkolesnik.ddkapi.dto.money;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author Alexandr Stegnin
 */

@Data
@Schema(name = "Investor", implementation = InvestorDTO.class, description = "Информация об инвесторе")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvestorDTO {

    @Schema(implementation = String.class, name = "Login", description = "Логин инвестора в системе")
    String login;

}
