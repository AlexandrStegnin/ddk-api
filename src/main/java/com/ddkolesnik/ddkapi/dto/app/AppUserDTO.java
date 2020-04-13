package com.ddkolesnik.ddkapi.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

/**
 * @author Alexandr Stegnin
 */

@Data
@Schema(name = "AppUser", description = "Информация об инвесторе")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppUserDTO {

    @NotBlank(message = "Код партнёра должен быть указан")
    @Schema(implementation = String.class, name = "partnerCode", description = "Код инвестора")
    String partnerCode;

    @NotBlank(message = "Фамилия инвестора должна быть указана")
    @Schema(implementation = String.class, name = "lastName", description = "Фамилия инвестора")
    String lastName;

    @Schema(implementation = String.class, name = "email", description = "Email инвестора")
    String email;

}
