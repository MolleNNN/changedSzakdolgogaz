package com.changedprogram.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaxNumberValidator implements ConstraintValidator<ValidTaxNumber, String> {

    private static final Logger logger = LoggerFactory.getLogger(TaxNumberValidator.class);

    @Override
    public void initialize(ValidTaxNumber constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String taxNumber, ConstraintValidatorContext context) {
        if (taxNumber == null || taxNumber.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.taxNumberNotProvided}")
                   .addConstraintViolation();
            return false;
        }

        if (taxNumber.length() != 10) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.taxNumberSize}")
                   .addConstraintViolation();
            return false;
        }

        if (!taxNumber.matches("\\d+")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.taxNumberPattern}")
                   .addConstraintViolation();
            return false;
        }

        int[] weights = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int sum = 0;

        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(taxNumber.charAt(i)) * weights[i];
            logger.debug("Sum after adding digit {}: {}", i, sum);
        }

        int checkDigit = sum % 11;
        logger.debug("Computed check digit: {}", checkDigit);

        boolean isValid = checkDigit == Character.getNumericValue(taxNumber.charAt(9));
        logger.debug("Tax number validation result: {}", isValid);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.invalidTaxNumber}")
                   .addConstraintViolation();
        }

        return isValid;
    }
}