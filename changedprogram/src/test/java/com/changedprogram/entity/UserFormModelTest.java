package com.changedprogram.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFormModelTest {

    private static final Logger logger = LoggerFactory.getLogger(UserFormModelTest.class);
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidUserFormModel() {
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Log any violations to understand why the test is failing
        if (!violations.isEmpty()) {
            violations.forEach(violation -> logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage()));
        }

        assertTrue(violations.isEmpty(), "There should be no violations");
    }

    @Test
    public void testInvalidEmailFormat() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example"); // Invalid email
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Assert
        violations.forEach(violation -> logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage()));
        assertEquals(1, violations.size(), "Expected one violation for invalid email format");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.emailFormat}")), 
                "Expected violation message to be '{error.adduser.emailFormat}'");
    }

    @Test
    public void testEmptyEmail() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail(""); // Empty email
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for empty email");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.emailNotEmpty}")), 
                "Expected violation message to be '{error.adduser.emailNotEmpty}'");
    }

    @Test
    public void testNullEmail() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail(null); // Null email
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for null email");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.emailNotNull}")), 
                "Expected violation message to be '{error.adduser.emailNotNull}'");
    }

    @Test
    public void testValidEmail() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com"); // Valid email
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Assert
        violations.forEach(violation -> logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage()));
        assertEquals(0, violations.size(), "Expected no violations for valid email");
    }

    
    @Test
    public void testNullPhoneNumber() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber(null); // Null phone number
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for null phone number");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.phoneNumber}")), 
                "Expected violation message to be '{error.adduser.phoneNumber}'");
    }

    @Test
    public void testEmptyPhoneNumber() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber(""); // Empty phone number
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for empty phone number");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.phoneNumber}")), 
                "Expected violation message to be '{error.adduser.phoneNumber}'");
    }

    @Test
    public void testPhoneNumberTooLong() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+123456789012345678901"); // Phone number too long (21 characters)
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for phone number too long");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.phoneNumberSize}")), 
                "Expected violation message to be '{error.adduser.phoneNumberSize}'");
    }

    @Test
    public void testInvalidPhoneNumberFormat() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("12345abcd0"); // Invalid phone number format
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for invalid phone number format");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.phoneNumberPattern}")), 
                "Expected violation message to be '{error.adduser.phoneNumberPattern}'");
    }

    @Test
    public void testValidPhoneNumber() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890"); // Valid phone number
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(0, violations.size(), "Expected no violations for valid phone number");
    }

    @Test
    public void testNullTaxNumber() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber(null); // Null tax number

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for null tax number");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.taxNumberNotProvided}")), 
                "Expected violation message to be '{error.adduser.taxNumberNotProvided}'");
    }

    @Test
    public void testEmptyTaxNumber() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber(""); // Empty tax number

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for empty tax number");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.taxNumberNotProvided}")), 
                "Expected violation message to be '{error.adduser.taxNumberNotProvided}'");
    }

    @Test
    public void testTaxNumberTooShort() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("12345"); // Tax number too short

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for tax number too short");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.taxNumberSize}")), 
                "Expected violation message to be '{error.adduser.taxNumberSize}'");
    }

    @Test
    public void testTaxNumberTooLong() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("123456789012345"); // Tax number too long

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for tax number too long");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.taxNumberSize}")), 
                "Expected violation message to be '{error.adduser.taxNumberSize}'");
    }

    @Test
    public void testTaxNumberWithNonDigits() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("1234abcd90"); // Tax number with non-digit characters

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for tax number with non-digit characters");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.taxNumberPattern}")), 
                "Expected violation message to be '{error.adduser.taxNumberPattern}'");
    }

    @Test
    public void testInvalidTaxNumberChecksum() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532033"); // Invalid tax number checksum

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for invalid tax number checksum");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.invalidTaxNumber}")), 
                "Expected violation message to be '{error.adduser.invalidTaxNumber}'");
    }

    @Test
    public void testValidTaxNumber() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // Valid tax number (assuming the checksum is correct)

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(0, violations.size(), "Expected no violations for valid tax number");
    }
    
    @Test
    public void testNullBirthdate() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(null); // Null birthdate
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); 

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for null birthdate");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.birthdate}")), 
                "Expected violation message to be '{error.adduser.birthdate}'");
    }

    @Test
    public void testFutureBirthdate() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365)); // Future date
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032");

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for future birthdate");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.birthdatePast}")), 
                "Expected violation message to be '{error.adduser.birthdatePast}'");
    }

    @Test
    public void testTooYoungBirthdate() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 10)); // Too young (10 years old)
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for being too young");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.birthdateTooYoung}")), 
                "Expected violation message to be '{error.adduser.birthdateTooYoung}'");
    }

    @Test
    public void testTooOldBirthdate() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 130)); // Too old (130 years old)
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for being too old");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.birthdateTooOld}")), 
                "Expected violation message to be '{error.adduser.birthdateTooOld}'");
    }

    @Test
    public void testValidBirthdate() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 25)); // Valid birthdate (25 years old)
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(0, violations.size(), "Expected no violations for valid birthdate");
    }
    
    @Test
    public void testNullName() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName(null); // Null name
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for null name");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.name}")), 
                "Expected violation message to be '{error.adduser.name}'");
    }

    @Test
    public void testEmptyName() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName(""); // Empty name
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for empty name");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.name}")), 
                "Expected violation message to be '{error.adduser.name}'");
    }

    @Test
    public void testNameTooLong() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("A".repeat(101)); // Name too long (101 characters)
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for name too long");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.nameSize}")), 
                "Expected violation message to be '{error.adduser.nameSize}'");
    }

    @Test
    public void testNameWithInvalidCharacters() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe!@#"); // Name with invalid characters
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for name with invalid characters");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.namePattern}")), 
                "Expected violation message to be '{error.adduser.namePattern}'");
    }

    @Test
    public void testValidName() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe"); // Valid name
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(0, violations.size(), "Expected no violations for valid name");
    }
    
    @Test
    public void testNullPositionId() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(null); // Null position ID
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for null position ID");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.positionId}")), 
                "Expected violation message to be '{error.adduser.positionId}'");
    }

    @Test
    public void testValidPositionId() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L); // Valid position ID
        user.setReceiverId(1L);
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(0, violations.size(), "Expected no violations for valid position ID");
    }
    @Test
    public void testNullReceiverId() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(null); // Null receiver ID
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for null receiver ID");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.receiverId}")), 
                "Expected violation message to be '{error.adduser.receiverId}'");
    }

    @Test
    public void testValidReceiverId() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L); // Valid position ID
        user.setReceiverId(1L); // Valid receiver ID
        user.setCompanyId(1L);
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(0, violations.size(), "Expected no violations for valid receiver ID");
    }
    @Test
    public void testNullCompanyId() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L);
        user.setReceiverId(1L);
        user.setCompanyId(null); // Null company ID
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(1, violations.size(), "Expected one violation for null company ID");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{error.adduser.companyId}")), 
                "Expected violation message to be '{error.adduser.companyId}'");
    }

    @Test
    public void testValidCompanyId() {
        // Arrange
        UserFormModel user = new UserFormModel();
        user.setName("John Doe");
        user.setBirthdate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 20)); // 20 years old
        user.setPositionId(1L); // Valid position ID
        user.setReceiverId(1L); // Valid receiver ID
        user.setCompanyId(1L); // Valid company ID
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+1234567890");
        user.setTaxNumber("8488532032"); // assuming valid

        // Act
        Set<ConstraintViolation<UserFormModel>> violations = validator.validate(user);

        // Debug: Print all violation messages
        violations.forEach(violation -> {
            logger.error("Violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });

        // Assert
        assertEquals(0, violations.size(), "Expected no violations for valid company ID");
    }

}
