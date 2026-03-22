package com.newgen.agent.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class MpinValidator implements ConstraintValidator<ValidMpin, String> {

    private static final Pattern MPIN_PATTERN = Pattern.compile("^\\d{4}$");

    @Override
    public boolean isValid(String mpin, ConstraintValidatorContext context) {
        if (mpin == null || mpin.isBlank()) {
            return true; // @NotBlank handles null/blank
        }
        return MPIN_PATTERN.matcher(mpin).matches();
    }
}
