package com.changedprogram.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.changedprogram.entity.Company;
import com.changedprogram.entity.Receiver;
import com.changedprogram.entity.Result;
import com.changedprogram.entity.User;
import com.changedprogram.repository.ResultRepository;

@Service
public class NotificationService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private EmailService emailService; // Use EmailService for sending emails

    @Scheduled(cron = "0 */2 * * * ?") // Runs every 2 minutes
    public void checkResultValidity() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 30);
        Date thirtyDaysAhead = c.getTime();

        List<Result> results = resultRepository.findResultsForNotification(thirtyDaysAhead);

        if (!results.isEmpty()) {
            try {
                emailService.sendEmailWithTemplate(results).get();
                for (Result result : results) {
                    result.setNotificationSent(true);
                    resultRepository.save(result);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}