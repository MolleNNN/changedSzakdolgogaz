package com.changedprogram.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Question text cannot be blank")
    @Size(min = 3, max = 255, message = "Question text must be between 4 and 255 characters")
    private String text;
    private boolean answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ppt_id")
    @JsonBackReference
    private Ppt ppt;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Ppt getPpt() {
		return ppt;
	}

	public void setPpt(Ppt ppt) {
		this.ppt = ppt;
	}
}