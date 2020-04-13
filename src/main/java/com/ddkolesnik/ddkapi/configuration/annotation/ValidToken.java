package com.ddkolesnik.ddkapi.configuration.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.ddkolesnik.ddkapi.util.Constant.INVALID_APP_TOKEN;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AppTokenValidator.class)
public @interface ValidToken {

    Class<?>[] groups() default {};

    String message() default INVALID_APP_TOKEN;

    Class<? extends Payload>[] payload() default {};

}
