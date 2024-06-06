package com.changedprogram.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller  // Marks this class as a Spring MVC controller
public class AdminController {

    @GetMapping("/admin")  // Handles GET requests for /admin
    public String getAdminInfo(Model model) {
        return "admin";  // Returns the view name "admin"
    }
    
    @GetMapping("/login")  // Handles GET requests for /login
    public String loginPage(Model model, HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());  // Retrieves the CSRF token from the request
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);  // Adds the CSRF token to the model
        }
        return "login";  // Returns the view name "login"
    }
    
    @GetMapping("/admin/logout")  // Handles GET requests for /admin/logout
    public String logout(HttpSession session) {
        session.setAttribute("logoutMessage", "Sikeresen kijeletntkeztél!");  // Adds a logout message to the session
        return "redirect:/logout";  // Redirects to the default Spring Security logout endpoint
    }
}