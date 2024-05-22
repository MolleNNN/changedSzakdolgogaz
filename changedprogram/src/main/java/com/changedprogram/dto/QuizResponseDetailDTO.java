package com.changedprogram.dto;

public class QuizResponseDetailDTO {
    private String questionText;
    private boolean response;
    private boolean correct;

    public QuizResponseDetailDTO(String questionText, boolean response, boolean correct) {
        this.questionText = questionText;
        this.response = response;
        this.correct = correct;
    }

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}
    
    
}