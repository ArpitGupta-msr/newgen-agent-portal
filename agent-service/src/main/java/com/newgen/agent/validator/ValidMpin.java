package com.newgen.agent.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MpinValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMpin {
    String message() default "MPIN must be exactly 4 digits.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
