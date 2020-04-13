package com.ddkolesnik.ddkapi.controller;

import com.ddkolesnik.ddkapi.dto.app.AppUserDTO;
import com.ddkolesnik.ddkapi.service.AppUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ddkolesnik.ddkapi.util.Constant.*;

/**
 * @author Alexandr Stegnin
 */

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(USERS)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "AppUser", description = "API для получения информации о пользователях системы")
public class AppUserController {

    AppUserService appUserService;

    @PutMapping(UPDATE_USER)
    public AppUserDTO update(@Valid @RequestBody AppUserDTO appUser) {
        return appUserService.update(appUser);
    }

}
