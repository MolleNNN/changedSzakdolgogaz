package com.changedprogram.entity;

import java.util.Date;
import java.util.List;

public class UserDTO {
    private User user;
    private List<SignatureDTO> signatures;
    private Date birthdate; // New field

    public UserDTO(User user, List<SignatureDTO> signatures, Date birthdate) {
        this.user = user;
        this.signatures = signatures;
        this.birthdate = birthdate; // Initialize new field
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<SignatureDTO> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<SignatureDTO> signatures) {
        this.signatures = signatures;
    }
    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }
}