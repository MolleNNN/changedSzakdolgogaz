package com.changedprogram.dto;

import java.util.Date;
import java.util.List;

public class UserrDTO {

    private Long id;
    private String name;
    private String email;
    private boolean active;
    private Date birthdate;
    private String positionName;
    private String receiverName;
    private String companyName;
    private String taxNumber;
    private String phoneNumber;
    
    private List<ResultDTO> results;


    // Constructors, getters, and setters
    public UserrDTO(Long id, String name, String email, boolean active, Date birthdate, String positionName, String receiverName, String companyName, String taxNumber, String phoneNumber, List<ResultDTO> results) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.active = active;
        this.birthdate = birthdate;
        this.positionName = positionName;
        this.receiverName = receiverName;
        this.companyName = companyName;
        this.taxNumber = taxNumber;
        this.phoneNumber = phoneNumber;
        this.results = results;
    }

    // Getters and setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

	public List<ResultDTO> getResults() {
		return results;
	}

	public void setResults(List<ResultDTO> results) {
		this.results = results;
	}

    
}