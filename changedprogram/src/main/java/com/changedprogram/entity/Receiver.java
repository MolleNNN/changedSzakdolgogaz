package com.changedprogram.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Receiver")
public class Receiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nem lehet üres mező")
    @Size(min = 2, max = 255, message = "2 és 255 karakter között kell lennie")
    @Pattern(
    	    regexp = "^[A-Za-zÀ-ÖØ-öø-ÿÁÉÍÓÖŐÚÜŰáéíóöőúüű\\-\\.\\s\\(\\)]+$", 
    	    message = "A név csak betűt, kötőjelet, pontot és zárójeleket tartalmazhat"
    	)
    @Column(name = "name", unique = true)
    private String name;
    
    @OneToMany(mappedBy = "receiver")  // Adjust the mapped field name if it's different in the User entity.
    @JsonIgnore
    private Set<User> users;
    
    // Default constructor
    public Receiver() {}

    // Constructor with name parameter
    public Receiver(String name) {
        this.name = name;
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
	public Set<User> getUsers() {
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
    
    
}
