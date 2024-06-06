package com.changedprogram.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.changedprogram.dto.CompanyDTO;
import com.changedprogram.entity.Company;
import com.changedprogram.service.AdminCompanyService;

import jakarta.validation.ConstraintViolationException;

@Controller  // Marks this class as a Spring MVC controller
public class AdminCompanyController {

    @Autowired  // Injects the AdminCompanyService bean
    private AdminCompanyService adminCompanyService;

    @GetMapping("/admin/companies")  // Handles GET requests for /admin/companies
    public String getAllCompanies(Model model) {
        List<CompanyDTO> companies = adminCompanyService.findAllCompanies();  // Retrieves a list of all companies
        model.addAttribute("companies", companies);  // Adds the companies list to the model
        model.addAttribute("newCompany", new Company());  // Adds a new Company object to the model
        return "companies";  // Returns the view name
    }

    @PostMapping("/admin/companies")  // Handles POST requests for /admin/companies
    @ResponseBody  // Indicates the return value will be written directly to the HTTP response body
    public ResponseEntity<Map<String, Object>> addCompany(@ModelAttribute("newCompany") Company company, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        try {
            Company savedCompany = adminCompanyService.addCompany(company);  // Adds a new company
            response.put("success", true);
            response.put("message", "A cég sikeresen hozzáadva!");
            response.put("company", savedCompany);
        } catch (ConstraintViolationException e) {
            response.put("success", false);
            response.put("message", e.getMessage().replaceAll("\\[.*?\\]", ""));  // Handles validation errors
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());  // Handles illegal argument errors
        }

        return ResponseEntity.ok(response);  // Returns the response as an HTTP entity
    }

    @GetMapping("/admin/getAllCompanies")  // Handles GET requests for /admin/getAllCompanies
    @ResponseBody  // Indicates the return value will be written directly to the HTTP response body
    public List<CompanyDTO> getAllCompanies() {
        return adminCompanyService.findAllCompanies();  // Returns a list of all companies
    }

    @PostMapping("/admin/updateCompany")  // Handles POST requests for /admin/updateCompany
    @ResponseBody  // Indicates the return value will be written directly to the HTTP response body
    public ResponseEntity<?> updateCompany(@RequestParam("companyId") Long companyId, @RequestParam("name") String newName) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminCompanyService.updateCompany(companyId, newName);  // Updates the company name
            response.put("success", true);
            response.put("message", "A cég sikeresen frissítve!");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());  // Handles illegal argument errors
        }
        return ResponseEntity.ok(response);  // Returns the response as an HTTP entity
    }

    @GetMapping("/admin/getDeletableCompanies")  // Handles GET requests for /admin/getDeletableCompanies
    @ResponseBody  // Indicates the return value will be written directly to the HTTP response body
    public List<Company> getDeletableCompanies() {
        return adminCompanyService.getDeletableCompanies();  // Returns a list of deletable companies
    }

    @PostMapping("/admin/deleteCompany/{companyId}")  // Handles POST requests for /admin/deleteCompany/{companyId}
    @ResponseBody  // Indicates the return value will be written directly to the HTTP response body
    public ResponseEntity<?> deleteCompany(@PathVariable Long companyId) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminCompanyService.deleteCompany(companyId);  // Deletes the company
            response.put("success", true);
            response.put("message", "A cég sikeresen törölve!");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());  // Handles illegal argument errors
        }
        return ResponseEntity.ok(response);  // Returns the response as an HTTP entity
    }
}
