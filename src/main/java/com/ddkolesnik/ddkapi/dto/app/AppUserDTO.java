package com.ddkolesnik.ddkapi.dto.app;

import com.ddkolesnik.ddkapi.model.app.AppUser;
import com.ddkolesnik.ddkapi.util.Kin;
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

    @Schema(implementation = String.class, name = "partnerId", description = "Код инвестора партнёра")
    String partnerId;

    @Schema(implementation = String.class, name = "kin", description = "Родственник/не родственник/супруг(а)")
    String kin;

    public AppUserDTO(AppUser appUser) {
        this.partnerCode = appUser.getLogin();
        this.lastName = appUser.getProfile().getLastName();
        this.email = appUser.getProfile().getEmail();
        this.partnerId = String.valueOf(appUser.getPartnerId());
        this.kin = extractKinTitle(appUser);
    }

    private String extractKinTitle(AppUser appUser) {
        Kin kin = Kin.fromId(appUser.getKin());
        if (kin != null) {
            return kin.getTitle();
        }
        return null;
    }

}
