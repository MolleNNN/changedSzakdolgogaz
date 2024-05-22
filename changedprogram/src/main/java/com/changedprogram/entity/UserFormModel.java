package com.changedprogram.entity;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.*;


public class UserFormModel {

    @NotBlank(message = "{error.adduser.name}")
    @Size(max = 100, message = "{error.adduser.nameSize}")
    @Pattern(regexp = "^[A-Za-zÀ-ÿŐő\\.\\s\\-]+[A-Za-zÀ-ÿŐő\\.\\s\\-]*$", message = "{error.adduser.namePattern}")
    private String name;

    @Past(message = "{error.adduser.birthdate}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthdate;

    @NotNull(message = "{error.adduser.positionId}")
    private Long positionId;

    @NotNull(message = "{error.adduser.receiverId}")
    private Long receiverId;

    @NotNull(message = "{error.adduser.companyId}")
    private Long companyId;

    @NotBlank(message = "{error.adduser.email}")
    @Email(message = "{error.adduser.emailFormat}")
    @Size(max = 255, message = "{error.adduser.emailSize}")
    private String email;

    @NotBlank(message = "{error.adduser.phoneNumber}")
    @Pattern(regexp = "\\+[0-9]{1,3}[0-9]{4,14}(?:x.+)?$", message = "{error.adduser.phoneNumberPattern}")
    @Size(max = 20, message = "{error.adduser.phoneNumberSize}")
    private String phoneNumber;

    @NotBlank(message = "{error.adduser.taxNumber}")
    @Size(min = 10, max = 10, message = "{error.adduser.taxNumberSize}")
    private String taxNumber;

    private Long pptId;

    private static final Logger logger = LoggerFactory.getLogger(UserFormModel.class);

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        char[] delimiters = { ' ', '-', '.' };
        this.name = WordUtils.capitalizeFully(name.trim(), delimiters);
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null) {
            this.email = email.trim().toLowerCase();
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber != null ? phoneNumber.trim() : null;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getPptId() {
        return pptId;
    }

    public void setPptId(Long pptId) {
        this.pptId = pptId;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public boolean isValidTaxNumber() {
        if (taxNumber == null) {
            logger.debug("Tax number is null");
            return false;
        }
        if (taxNumber.length() != 10) {
            logger.debug("Tax number length is not 10: {}", taxNumber.length());
            return false;
        }
        if (!taxNumber.matches("\\d+")) {
            logger.debug("Tax number contains non-digit characters: {}", taxNumber);
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
        return isValid;
    }

    public void validateBirthdate() throws Exception {
        if (birthdate == null) {
            throw new Exception(getMessage("error.adduser.birthdate"));
        }

        LocalDate today = LocalDate.now();
        LocalDate birthday = birthdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate maxAgeDate = today.minusYears(120);
        LocalDate minAgeDate = today.minusYears(14);

        if (birthday.isAfter(minAgeDate)) {
            throw new Exception(getMessage("error.adduser.birthdateTooYoung"));
        } else if (birthday.isBefore(maxAgeDate)) {
            throw new Exception(getMessage("error.adduser.birthdateTooOld"));
        }
    }

    public String getMessage(String code) {
        return ResourceBundle.getBundle("messages", LocaleContextHolder.getLocale()).getString(code);
    }
}