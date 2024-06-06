package com.changedprogram.entity;

import java.util.Base64;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "Result")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ppt_id")
    @JsonBackReference
    private Ppt ppt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user; // Link to the user who completed the quiz
    
    @Column(name = "valid")
    @Temporal(TemporalType.DATE)
    private Date valid;

    @Lob
    @Column(name = "signature", columnDefinition = "LONGBLOB")
    private byte[] signature;

    @Column(name = "filled")
    @Temporal(TemporalType.DATE)
    private Date filled; // Store the submission date
    
    @Column(name = "notification_sent")
    private boolean notificationSent = false;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Ppt getPpt() {
		return ppt;
	}

	public void setPpt(Ppt ppt) {
		this.ppt = ppt;
	}

	public Date getValid() {
		return valid;
	}

	public void setValid(Date valid) {
		this.valid = valid;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
    
    public String getSignatureBase64() {
        return this.signature != null ? "data:image/png;base64," + Base64.getEncoder().encodeToString(this.signature) : null;
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
	
}
