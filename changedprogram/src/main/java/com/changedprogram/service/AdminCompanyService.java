package com.changedprogram.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.changedprogram.dto.CompanyDTO;
import com.changedprogram.entity.Company;
import com.changedprogram.repository.CompanyRepository;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@Service
public class AdminCompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private Validator validator;
/*
    public List<CompanyDTO> findAllCompanies() {
        return companyRepository.findAll().stream()
                .map(company -> new CompanyDTO(company.getId(), company.getName()))
              
                .collect(Collectors.toList());
    }
*/
    public List<CompanyDTO> findAllCompanies() {
        // Use the sorted query to fetch companies in alphabetical order
        return companyRepository.findAllCompanyDTOsSortedByName();
    }

    public Company addCompany(Company company) throws ConstraintViolationException {
        validateCompany(company);
        String formattedName = formatCompanyName(company.getName());
        company.setName(formattedName);

        Optional<Company> existingCompany = companyRepository.findByNameIgnoreCase(formattedName);
        if (existingCompany.isPresent()) {
            throw new IllegalArgumentException("Company already exists!");
        }

        return companyRepository.save(company);
    }

    public void updateCompany(Long companyId, String newName) {
        newName = newName.trim();
        validateCompanyName(newName);
        Company existingCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found, please refresh the page"));

        String formattedName = formatCompanyName(newName);
        if (companyRepository.existsByName(formattedName)) {
            throw new IllegalArgumentException("Company already exists.");
        }

        existingCompany.setName(formattedName);
        companyRepository.save(existingCompany);
    }

    public List<Company> getDeletableCompanies() {
        return companyRepository.findAll().stream()
                .filter(company -> company.getUsers().isEmpty())
                .collect(Collectors.toList());
    }

    public void deleteCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found, please refresh the page"));

        if (!company.getUsers().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete company because it is in use, please refresh the page");
        }

        companyRepository.delete(company);
    }

    private void validateCompany(Company company) throws ConstraintViolationException {
        // Trim the name before validation
        company.setName(company.getName().trim());
        
        Set<ConstraintViolation<Company>> violations = validator.validate(company);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("<br>"));
            throw new ConstraintViolationException(errorMessage, violations);
        }
    }

    private void validateCompanyName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be blank.");
        }

        if (name.length() < 2 || name.length() > 255) {
            throw new IllegalArgumentException("Company name must be between 2 and 255 characters.");
        }
        
        // Removed pattern matching to allow any character
    }

    private String formatCompanyName(String name) {
        return capitalizeFirstLetter(name.trim().replaceAll("\\s+", " "));
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}