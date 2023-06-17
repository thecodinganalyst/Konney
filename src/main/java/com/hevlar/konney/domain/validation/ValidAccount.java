package com.hevlar.konney.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Target({TYPE, FIELD, PARAMETER, RECORD_COMPONENT})
@Constraint(validatedBy = AccountValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAccount {
    String message() default "Account data not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
