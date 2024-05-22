package com.changedprogram.dto;

public class UserQuizAttemptDTO {
    private Long id;
    private String name;
    private long attempts;

    public UserQuizAttemptDTO(Long id, String name, long attempts) {
        this.id = id;
        this.name = name;
        this.attempts = attempts;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getAttempts() {
		return attempts;
	}

	public void setAttempts(long attempts) {
		this.attempts = attempts;
	}

    // getters and setters
    
    
}