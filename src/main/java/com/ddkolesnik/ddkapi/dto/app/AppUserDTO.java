package com.ddkolesnik.ddkapi.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author Alexandr Stegnin
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AppUser", description = "Информация об инвесторе")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppUserDTO {

    @NotBlank(message = "Код инвестора должен быть указан")
    @Pattern(regexp = "\\d+", message = "Код инвестора должен содержать только цифры")
    @Schema(implementation = String.class, name = "partnerCode", description = "Код инвестора")
    String partnerCode;

    @NotBlank(message = "Фамилия инвестора должна быть указана")
    @Schema(implementation = String.class, name = "lastName", description = "Фамилия инвестора")
    String lastName;

    @Schema(implementation = String.class, name = "email", description = "Email инвестора")
    String email;

}
