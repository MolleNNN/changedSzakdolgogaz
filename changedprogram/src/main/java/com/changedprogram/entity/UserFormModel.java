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

import com.changedprogram.validation.ValidBirthdate;
import com.changedprogram.validation.ValidEmail;
import com.changedprogram.validation.ValidName;
import com.changedprogram.validation.ValidPhoneNumber;
import com.changedprogram.validation.ValidTaxNumber;

import jakarta.validation.constraints.*;


public class UserFormModel {

    @ValidName
    private String name;

    @ValidBirthdate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthdate;

    @NotNull(message = "{error.adduser.positionId}")
    private Long positionId;

    @NotNull(message = "{error.adduser.receiverId}")
    private Long receiverId;

    @NotNull(message = "{error.adduser.companyId}")
    private Long companyId;

    @ValidEmail
    private String email;

    @ValidPhoneNumber
    private String phoneNumber;

    @ValidTaxNumber
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
    public String getMessage(String code) {
        return ResourceBundle.getBundle("messages", LocaleContextHolder.getLocale()).getString(code);
    }
}