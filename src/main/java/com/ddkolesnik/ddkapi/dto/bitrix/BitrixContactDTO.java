package com.ddkolesnik.ddkapi.dto.bitrix;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Data
@Schema(name = "Bitrix Information", implementation = BitrixContactDTO.class, description = "Информация из битрикс системы")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BitrixContactDTO {

    @Schema(implementation = String.class, name = "name", description = "Имя инвестора")
    String name;

    @Schema(implementation = String.class, name = "secondName", description = "Отчество инвестора")
    String secondName;

    @Schema(implementation = String.class, name = "lastName", description = "Фамилия инвестора")
    String lastName;

    @Schema(implementation = LocalDate.class, name = "birthday", description = "Дата рождения")
    LocalDate birthday;

    @Schema(implementation = String.class, name = "code", description = "Код партнёра")
    String code;

    @ArraySchema(schema = @Schema(implementation = BitrixEmailDTO.class,
            name = "emails", description = "Адреса электронной почты инвестора"))
    Set<BitrixEmailDTO> emails = new HashSet<>();

}
