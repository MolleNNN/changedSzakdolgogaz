package com.changedprogram.service;

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

    @Value("${notification.email}")
    private String notificationEmail;

   /* @Async
    public CompletableFuture<Void> sendSimpleMessage(String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ehs-reports@outlook.com");
        message.setTo(notificationEmail); // Using the configured notification email
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
        return CompletableFuture.completedFuture(null);
    }*/
    
/*    @Async
    public CompletableFuture<Void> sendEmailWithTemplate(User user, Result result, Receiver receiver, Company company) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("result", result);
            context.setVariable("receiver", receiver);
            context.setVariable("company", company);

            String htmlContent = templateEngine.process("reminder-email.html", context);

            helper.setFrom("ehs-reports@outlook.com"); // This should match the SMTP authenticated user
            helper.setTo(notificationEmail); // Sending to a configured recipient
            helper.setSubject("Reminder: Upcoming Expiry");
            helper.setText(htmlContent, true); // true indicates HTML content

            emailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Failed to send email", e);
            throw new RuntimeException(e);
        }
        return CompletableFuture.completedFuture(null);
    }*/
    
  /*  @Async
    public CompletableFuture<Void> sendEmailWithTemplate(User user, Result result, Receiver receiver, Company company) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("result", result);
            context.setVariable("receiver", receiver);
            context.setVariable("company", company);

            String htmlContent = templateEngine.process("reminder-email.html", context);

            helper.setFrom("ehs-reports@outlook.com"); // Ensure this matches your SMTP username
            helper.setTo(notificationEmail);
            helper.setSubject("Reminder: Upcoming Expiry");
            helper.setText(htmlContent, true); // true indicates HTML content

            emailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Failed to send email", e);
            throw new RuntimeException(e);
        }
        return CompletableFuture.completedFuture(null);
    }
*/
    
    @Async
    public CompletableFuture<Void> sendEmailWithTemplate(User user, Result result, Receiver receiver, Company company) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Create context and add variables
            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("result", result);
            context.setVariable("receiver", receiver);
            context.setVariable("company", company);

            // Process the template with the context
            String htmlContent = templateEngine.process("reminder-email.html", context);

            // Set email details
            helper.setFrom("ehs-reports@outlook.com"); // Ensure this matches your SMTP username
            helper.setTo("aimzvac@gmail.com"); // Change this to the recipient's email
            helper.setSubject("Reminder: Upcoming Expiry");
            helper.setText(htmlContent, true); // true indicates HTML content

            // Send the email
            emailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Failed to send email", e);
            throw new RuntimeException(e);
        }
        return CompletableFuture.completedFuture(null);
    }
    
}