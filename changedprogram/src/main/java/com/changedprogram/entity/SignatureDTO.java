package com.changedprogram.entity;

import java.util.Date;

public class SignatureDTO {
    private String pptFilename;
    private String decryptedSignature;
    private Date valid;
    private Date filled;

    public SignatureDTO(String pptFilename, String decryptedSignature, Date valid, Date filled) {
        this.pptFilename = pptFilename;
        this.decryptedSignature = decryptedSignature;
        this.valid = valid;
        this.filled = filled; // Initialize new field
    }

    // Getters and Setters
    public String getPptFilename() {
        return pptFilename;
    }

    public void setPptFilename(String pptFilename) {
        this.pptFilename = pptFilename;
    }

    public String getDecryptedSignature() {
        return decryptedSignature;
    }

    public void setDecryptedSignature(String decryptedSignature) {
        this.decryptedSignature = decryptedSignature;
    }

    public Date getValid() {
        return valid;
    }

    public void setValid(Date valid) {
        this.valid = valid;
    }

    public Date getFilled() {
        return filled;
    }

    public void setFilled(Date filled) {
        this.filled = filled;
    }
}