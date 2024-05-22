package com.changedprogram.dto;

public class QuizAttemptDTO {
    private Long id;
    private String pptName;
    private int attemptNumber;
    private String timestamp;

    public QuizAttemptDTO(Long id, String pptName, int attemptNumber, String timestamp) {
        this.id = id;
        this.pptName = pptName;
        this.attemptNumber = attemptNumber;
        this.timestamp = timestamp;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPptName() {
		return pptName;
	}

	public void setPptName(String pptName) {
		this.pptName = pptName;
	}

	public int getAttemptNumber() {
		return attemptNumber;
	}

	public void setAttemptNumber(int attemptNumber) {
		this.attemptNumber = attemptNumber;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

    // getters and setters
    
}