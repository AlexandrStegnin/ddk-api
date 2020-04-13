package com.ddkolesnik.ddkapi;

import com.ddkolesnik.ddkapi.dto.app.AppUserDTO;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alexandr Stegnin
 */

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Проверка заполненности полей пользователя")
public class AppUserDTOConstraintsTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @Order(1)
    @DisplayName("Проверка фамилии на null, должна быть 1 ошибка")
    public void when_last_name_null_the_constraint_exceptions_not_equals_zero() {
        AppUserDTO dto = new AppUserDTO();
        dto.setPartnerCode("login");
        Set<ConstraintViolation<AppUserDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 2);
    }

    @Test
    @Order(2)
    @DisplayName("Проверка фамилии на пустую строку, должна быть 1 ошибка")
    public void when_last_name_is_empty_the_constraint_exceptions_not_equals_zero() {
        AppUserDTO dto = new AppUserDTO();
        dto.setPartnerCode("login");
        dto.setLastName("");
        Set<ConstraintViolation<AppUserDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 2);
    }

    @Test
    @Order(3)
    @DisplayName("Проверка фамилии на строку с пробелом, должна быть 1 ошибка")
    public void when_last_name_is_blank_the_constraint_exceptions_not_equals_zero() {
        AppUserDTO dto = new AppUserDTO();
        dto.setPartnerCode("login");
        dto.setLastName(" ");
        Set<ConstraintViolation<AppUserDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 2);
    }

    @Test
    @Order(4)
    @DisplayName("Проверка кода партнёра, содержит не только цифры")
    public void when_partner_code_is_digit_with_character_the_constraint_exceptions_not_equals_zero() {
        AppUserDTO dto = new AppUserDTO();
        dto.setPartnerCode("login123");
        dto.setLastName(" ");
        Set<ConstraintViolation<AppUserDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 2);
    }

    @Test
    @Order(5)
    @DisplayName("Проверка кода партнёра, содержит только цифры")
    public void when_user_is_valid_then_constraint_exceptions_equals_zero() {
        AppUserDTO dto = new AppUserDTO();
        dto.setPartnerCode("000123");
        dto.setLastName("last name");
        Set<ConstraintViolation<AppUserDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 0);
    }

}
