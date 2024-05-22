package com.changedprogram.dto;

import java.util.Date;

public class ResultDTO {

    private Long id;
    private Date filled;
    private boolean notificationSent;
    private Date valid;
    private String pptName;

    private String signature; // New field for the encrypted signature

    // Constructor
    public ResultDTO(Long id, Date filled, boolean notificationSent, Date valid, String pptName, String signature) {
        this.id = id;
        this.filled = filled;
        this.notificationSent = notificationSent;
        this.valid = valid;
        this.pptName = pptName;
        this.signature = signature; // Initialize it
    }
	// Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFilled() {
        return filled;
    }

    public void setFilled(Date filled) {
        this.filled = filled;
    }

    public boolean isNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public Date getValid() {
        return valid;
    }

    public void setValid(Date valid) {
        this.valid = valid;
    }

    public String getPptName() {
        return pptName;
    }

    public void setPptName(String pptName) {
        this.pptName = pptName;
    }
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}

}