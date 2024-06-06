package com.changedprogram.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Language")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String code; // e.g., 'en', 'hu'

    @Column(nullable = false, length = 255)
    private String name; // e.g., 'English', 'Hungarian'

    @OneToMany(mappedBy = "language")
    private Set<Ppt> ppts;

    
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public Set<Ppt> getPpts() {
		return ppts;
	}

	public void setPpts(Set<Ppt> ppts) {
		this.ppts = ppts;
	}

    
}