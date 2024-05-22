package com.changedprogram.dto;

public class QuizResponseDTO {
    private Long id;
    private String questionName;
    private boolean response;
    private boolean isCorrect;

    // No-argument constructor
    public QuizResponseDTO() {
    }
    
    public QuizResponseDTO(Long id, String questionName, boolean response, boolean isCorrect) {
        this.id = id;
        this.questionName = questionName;
        this.response = response;
        this.isCorrect = isCorrect;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQuestionName() {
		return questionName;
	}

	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

    // getters and setters
    
}