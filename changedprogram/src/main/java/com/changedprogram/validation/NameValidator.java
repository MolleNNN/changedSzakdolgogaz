package com.changedprogram.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<ValidName, String> {

    private static final String NAME_PATTERN = "^[A-Za-zÀ-ÿŐő\\.\\s\\-]+[A-Za-zÀ-ÿŐő\\.\\s\\-]*$";

    @Override
    public void initialize(ValidName constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null || name.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.name}")
                   .addConstraintViolation();
            return false;
        }

        if (name.length() > 100) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.nameSize}")
                   .addConstraintViolation();
            return false;
        }

        if (!name.matches(NAME_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.namePattern}")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}