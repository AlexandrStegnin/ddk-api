package com.ddkolesnik.ddkapi.dto.app;

import com.ddkolesnik.ddkapi.model.app.AppUser;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

/**
 * @author Alexandr Stegnin
 */

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppUserDTO {

    @NotBlank(message = "Код партнёра должен быть указан")
    String login;

    @NotBlank(message = "Фамилия инвестора должна быть указана")
    String lastName;

    String email;

    public AppUserDTO(AppUser entity) {
        this.login = entity.getLogin();
        this.lastName = entity.getLastName();
        this.email = entity.getEmail();
    }

}
