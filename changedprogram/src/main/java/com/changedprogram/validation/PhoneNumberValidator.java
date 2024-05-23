package com.changedprogram.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final String PHONE_PATTERN = "\\+[0-9]{1,3}[0-9]{4,14}(?:x.+)?$";

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.phoneNumber}")
                   .addConstraintViolation();
            return false;
        }

        if (phoneNumber.length() > 20) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.phoneNumberSize}")
                   .addConstraintViolation();
            return false;
        }

        if (!phoneNumber.matches(PHONE_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.phoneNumberPattern}")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}
