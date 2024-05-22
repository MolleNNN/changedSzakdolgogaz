package com.changedprogram.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.changedprogram.service.EmailService;

@RestController
public class EmailTestController {

    @Autowired
    private EmailService emailService;
/*
    @GetMapping("/test-email")
    public String sendTestEmail() {
        emailService.sendSimpleMessage("Test Subject", "This is a test email.");
        return "Email sent!";
    }*/
}
