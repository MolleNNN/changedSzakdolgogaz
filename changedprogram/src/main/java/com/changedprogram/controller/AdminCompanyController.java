package com.changedprogram.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.changedprogram.dto.CompanyDTO;
import com.changedprogram.entity.Company;
import com.changedprogram.repository.CompanyRepository;
import com.changedprogram.service.AdminCompanyService;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@Controller
public class AdminCompanyController {

    @Autowired
    private AdminCompanyService adminCompanyService;

    @GetMapping("/admin/companies")
    public String getAllCompanies(Model model) {
        List<CompanyDTO> companies = adminCompanyService.findAllCompanies();
        model.addAttribute("companies", companies);
        model.addAttribute("newCompany", new Company());
        return "companies";
    }

    @PostMapping("/admin/companies")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addCompany(@ModelAttribute("newCompany") Company company, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        try {
            Company savedCompany = adminCompanyService.addCompany(company);
            response.put("success", true);
            response.put("message", "Company added successfully!");
            response.put("company", savedCompany);
        } catch (ConstraintViolationException e) {
            response.put("success", false);
            response.put("message", e.getMessage().replaceAll("\\[.*?\\]", ""));
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/getAllCompanies")
    @ResponseBody
    public List<CompanyDTO> getAllCompanies() {
        return adminCompanyService.findAllCompanies();
    }

    @PostMapping("/admin/updateCompany")
    @ResponseBody
    public ResponseEntity<?> updateCompany(@RequestParam("companyId") Long companyId, @RequestParam("name") String newName) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminCompanyService.updateCompany(companyId, newName);
            response.put("success", true);
            response.put("message", "Company updated successfully.");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/getDeletableCompanies")
    @ResponseBody
    public List<Company> getDeletableCompanies() {
        return adminCompanyService.getDeletableCompanies();
    }

    @PostMapping("/admin/deleteCompany/{companyId}")
    @ResponseBody
    public ResponseEntity<?> deleteCompany(@PathVariable Long companyId) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminCompanyService.deleteCompany(companyId);
            response.put("success", true);
            response.put("message", "Company deleted successfully!");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}