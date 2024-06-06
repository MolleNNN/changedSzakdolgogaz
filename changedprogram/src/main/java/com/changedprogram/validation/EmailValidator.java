package com.changedprogram.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.emailNotNull}")
                   .addConstraintViolation();
            return false;
        }

        if (email.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.emailNotEmpty}")
                   .addConstraintViolation();
            return false;
        }

        if (email.length() > 255) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.emailSize}")
                   .addConstraintViolation();
            return false;
        }

        if (!email.matches(EMAIL_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.emailFormat}")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}