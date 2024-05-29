package com.changedprogram.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class QuestionForm {
    
    @NotBlank(message = "Question text cannot be blank")
    @Size(min = 4, max = 255, message = "Question text must be between 4 and 255 characters")
    private String text;

    private boolean answer;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isAnswer() {
		return answer;
	}

	public void setAnswer(boolean answer) {
		this.answer = answer;
	}

    // Getters and setters
    
    
}