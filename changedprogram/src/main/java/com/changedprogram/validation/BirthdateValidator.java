package com.changedprogram.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class BirthdateValidator implements ConstraintValidator<ValidBirthdate, Date> {

    @Override
    public void initialize(ValidBirthdate constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Date birthdate, ConstraintValidatorContext context) {
        if (birthdate == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.birthdate}")
                   .addConstraintViolation();
            return false;
        }

        LocalDate birthdateLocal = birthdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        LocalDate maxAgeDate = today.minusYears(120);
        LocalDate minAgeDate = today.minusYears(14);

        if (birthdateLocal.isAfter(today)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.birthdatePast}")
                   .addConstraintViolation();
            return false;
        }

        if (birthdateLocal.isAfter(minAgeDate)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.birthdateTooYoung}")
                   .addConstraintViolation();
            return false;
        }

        if (birthdateLocal.isBefore(maxAgeDate)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.adduser.birthdateTooOld}")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}