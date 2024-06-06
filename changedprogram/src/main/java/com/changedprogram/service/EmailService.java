package com.changedprogram.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.changedprogram.entity.Company;
import com.changedprogram.entity.Receiver;
import com.changedprogram.entity.Result;
import com.changedprogram.entity.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender emailSender;
    
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${notification.email.from}")
    private String fromEmail;

    @Value("${notification.email.to}")
    private String toEmail;

    @Async
    public CompletableFuture<Void> sendEmailWithTemplate(List<Result> results) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Create context and add variables
            Context context = new Context();
            context.setVariable("results", results);

            // Process the template with the context
            String htmlContent = templateEngine.process("reminder-email.html", context);

            // Set email details
            helper.setFrom(fromEmail); // Ensure this matches your SMTP username
            helper.setTo(toEmail); // Change this to the recipient's email
            helper.setSubject("Emlékeztető: Közelgő lejárati idő");
            helper.setText(htmlContent, true); // true indicates HTML content

            // Send the email
            emailSender.send(message);
            logger.info("Email sent successfully to {}", toEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send email", e);
            throw new RuntimeException(e);
        }
        return CompletableFuture.completedFuture(null);
    }
}