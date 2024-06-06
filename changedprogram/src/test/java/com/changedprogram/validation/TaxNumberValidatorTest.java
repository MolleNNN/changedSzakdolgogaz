package com.changedprogram.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import static org.mockito.Mockito.*;

public class TaxNumberValidatorTest {

    private TaxNumberValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        validator = new TaxNumberValidator();

        // Mocking behavior for context and violationBuilder
        when(context.buildConstraintViolationWithTemplate(anyString())).thenAnswer(new Answer<ConstraintValidatorContext.ConstraintViolationBuilder>() {
            @Override
            public ConstraintValidatorContext.ConstraintViolationBuilder answer(InvocationOnMock invocation) {
                return violationBuilder;
            }
        });

        when(violationBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    public void whenTaxNumberIsNull_thenInvalid() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    public void whenTaxNumberIsEmpty_thenInvalid() {
        assertFalse(validator.isValid("", context));
    }

    @Test
    public void whenTaxNumberHasIncorrectLength_thenInvalid() {
        assertFalse(validator.isValid("123456789", context)); // 9 digits
        assertFalse(validator.isValid("12345678901", context)); // 11 digits
    }

    @Test
    public void whenTaxNumberHasNonDigitCharacters_thenInvalid() {
        assertFalse(validator.isValid("12345abcd0", context));
    }

    @Test
    public void whenTaxNumberHasInvalidCheckDigit_thenInvalid() {
        assertFalse(validator.isValid("1234567890", context)); // Assuming 0 is incorrect
    }

    @Test
    public void whenTaxNumberIsValid_thenValid() {
        // Example with valid tax number. Replace with a valid one if the check digit logic changes.
        assertTrue(validator.isValid("8488532032", context));
    }
}
