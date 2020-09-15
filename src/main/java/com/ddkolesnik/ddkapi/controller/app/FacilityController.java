package com.ddkolesnik.ddkapi.controller.app;

import com.ddkolesnik.ddkapi.configuration.annotation.ValidToken;
import com.ddkolesnik.ddkapi.configuration.exception.ApiErrorResponse;
import com.ddkolesnik.ddkapi.configuration.exception.ApiSuccessResponse;
import com.ddkolesnik.ddkapi.dto.money.FacilityDTO;
import com.ddkolesnik.ddkapi.service.money.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ddkolesnik.ddkapi.util.Constant.FACILITIES;
import static com.ddkolesnik.ddkapi.util.Constant.UPDATE_FACILITY;

/**
 * @author Alexandr Stegnin
 */

@SuppressWarnings("unused")
@Slf4j
@Validated
@RestController
@RequestMapping(FACILITIES)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "Facility", description = "API для обновления информации об объектах")
public class FacilityController {

    FacilityService facilityService;

    @Operation(summary = "Добавить/изменить объект", tags = {"Facility"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiSuccessResponse.class)))),
            @ApiResponse(responseCode = "Error", description = "Произошла ошибка",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorResponse.class))))})
    @PostMapping(path = UPDATE_FACILITY, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiSuccessResponse update(@Parameter(description = "Ключ приложения.", schema = @Schema(implementation = String.class))
                                     @PathVariable(name = "token") @ValidToken String token,
                                     @Parameter(description = "Объект", schema = @Schema(implementation = FacilityDTO.class))
                                     @Valid @RequestBody FacilityDTO facility) {
        facilityService.update(facility);
        log.info("Объект успешно обновлён [{}]", facility);
        return new ApiSuccessResponse(HttpStatus.OK, "Объект успешно сохранён");
    }

}
