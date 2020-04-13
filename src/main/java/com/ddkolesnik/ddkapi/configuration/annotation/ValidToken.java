package com.ddkolesnik.ddkapi.configuration.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AppTokenValidator.class)
public @interface ValidToken {

    Class<?>[] groups() default {};

    String message() default "Не верный ключ приложения.";

    Class<? extends Payload>[] payload() default {};

}
