package com.changedprogram.dto;

import java.time.LocalDateTime;

public class QuizAttemptDetailDTO {
    private Long id; // Add this field
    private String pptFileName;
    private int attemptNumber;
    private int percentage;
    private LocalDateTime timestamp;
    private String languageName;
    private String typeName;

    public QuizAttemptDetailDTO(Long id, String pptFileName, int attemptNumber, int percentage, LocalDateTime timestamp, String languageName, String typeName) {
        this.id = id;
        this.pptFileName = pptFileName;
        this.attemptNumber = attemptNumber;
        this.percentage = percentage;
        this.timestamp = timestamp;
        this.languageName = languageName;
        this.typeName = typeName;
    }

	public String getPptFileName() {
		return pptFileName;
	}

	public void setPptFileName(String pptFileName) {
		this.pptFileName = pptFileName;
	}

	public int getAttemptNumber() {
		return attemptNumber;
	}

	public void setAttemptNumber(int attemptNumber) {
		this.attemptNumber = attemptNumber;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    // getters and setters
    
    
}