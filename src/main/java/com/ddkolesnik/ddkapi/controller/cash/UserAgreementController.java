package com.ddkolesnik.ddkapi.controller.cash;

import com.ddkolesnik.ddkapi.configuration.annotation.ValidToken;
import com.ddkolesnik.ddkapi.configuration.exception.ApiErrorResponse;
import com.ddkolesnik.ddkapi.configuration.exception.ApiSuccessResponse;
import com.ddkolesnik.ddkapi.dto.cash.UserAgreementDTO;
import com.ddkolesnik.ddkapi.service.cash.UserAgreementService;
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

import static com.ddkolesnik.ddkapi.util.Constant.PATH_USER_AGREEMENT;
import static com.ddkolesnik.ddkapi.util.Constant.PATH_USER_AGREEMENT_UPDATE;

/**
 * @author Alexandr Stegnin
 */

@Slf4j
@Validated
@RestController
@RequestMapping(PATH_USER_AGREEMENT)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "UserAgreement", description = "API для создания записи о том, с кем заключён договор по проекту")
public class UserAgreementController {

    UserAgreementService userAgreementService;

    @Operation(summary = "Создание записи на основании данных из Битрикс 24", tags = {"UserAgreement"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiSuccessResponse.class)))),
            @ApiResponse(responseCode = "Error", description = "Произошла ошибка",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorResponse.class))))})
    @PostMapping(value = PATH_USER_AGREEMENT_UPDATE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiSuccessResponse updateUserAgreement(@Parameter(description = "Ключ приложения.", schema = @Schema(implementation = String.class))
                                                 @PathVariable(name = "token") @ValidToken String token,
                                                 @Parameter(description = "Данные из Битрикс 24", schema = @Schema(implementation = UserAgreementDTO.class))
                                                 @Valid UserAgreementDTO dto) {
        userAgreementService.update(dto);
        log.info("Запись успешно обновлена [{}]", dto);
        return new ApiSuccessResponse(HttpStatus.OK, "Данные успешно сохранены");
    }

}
