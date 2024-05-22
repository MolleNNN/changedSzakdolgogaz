package com.changedprogram.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

  
    // Daily check
    //@Scheduled(cron = "0 0 1 * * ?") // Runs at 1 AM every day
  
 //   @Scheduled(cron = "0 */10 * * * ?") // Runs every 10 minutes
   // public void checkResultValidity() {
 //       Date today = new Date();
   //     Calendar c = Calendar.getInstance();
   //     c.setTime(today);
     //   c.add(Calendar.DATE, 30);
       // Date thirtyDaysAhead = c.getTime();
//
  //      List<Result> results = resultRepository.findResultsForNotification(thirtyDaysAhead);
//
  //      for (Result result : results) {
    //        if (!result.isNotificationSent()) {
      //          sendNotificationEmail(result);
        //        result.setNotificationSent(true);
//                resultRepository.save(result);
  //          }
    //    }
   // }
    
  //  @Scheduled(cron = "0 */10 * * * ?") // Runs every 10 minutes
  
 //   @Scheduled(cron = "0 */5 * * * ?") // Runs every 5 minutes
/*    public void checkResultValidity() {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE, 30);
        Date thirtyDaysAhead = c.getTime();

        List<Result> results = resultRepository.findResultsForNotification(thirtyDaysAhead);

        for (Result result : results) {
            if (!result.isNotificationSent()) {
                try {
                    sendNotificationEmail(result);
                    result.setNotificationSent(true);
                    resultRepository.save(result);
                } catch (ExecutionException | InterruptedException e) {
                    // Handle exceptions, e.g., log them or take specific actions
                    e.printStackTrace();
                }
            }
        }
    }
*/
    
  

    private boolean shouldNotify(Result result) {
        // Check for newer valid results
        List<Result> newerResults = resultRepository.findByUserIdAndValidAfter(result.getUser().getId(), result.getValid());
        return newerResults.isEmpty();
    }

  /*  private void sendNotificationEmail(Result result) {
        User user = result.getUser();
        String subject = "Reminder: Upcoming Expiry";
        String text = "Dear " + user.getName() + ",\n\nYour validation for " + result.getPpt().getFilename() + " will expire soon. Please take necessary action.\n\nRegards,\nAdmin";
        emailService.sendSimpleMessage(subject, text);
    }*/
    
 /*   private void sendNotificationEmail(Result result) throws ExecutionException, InterruptedException {
        User user = result.getUser();
        // Placeholder for fetching Receiver and Company. Replace with actual logic.
        Receiver receiver = new Receiver(); // Assuming a constructor or a finder method exists
        Company company = new Company(); // Assuming a constructor or a finder method exists

        receiver.setName("Receiver Name"); // Example setting
        company.setName("Company Name"); // Example setting

        // Now call the email service to send the email
        emailService.sendEmailWithTemplate(user, result, receiver, company).get(); // Using .get() to ensure asynchronous handling completes
    }*/
    

    @Scheduled(cron = "0 */5 * * * ?") // Runs every 5 minutes
    public void checkResultValidity() {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE, 30);
        Date thirtyDaysAhead = c.getTime();

        List<Result> results = resultRepository.findResultsForNotification(thirtyDaysAhead);

        for (Result result : results) {
            if (!result.isNotificationSent()) {
                try {
                    sendNotificationEmail(result);
                    result.setNotificationSent(true);
                    resultRepository.save(result);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendNotificationEmail(Result result) throws ExecutionException, InterruptedException {
        User user = result.getUser();
        Receiver receiver = user.getReceiver(); // Directly fetch from the eager loaded user
        Company company = user.getCompany(); // Directly fetch from the eager loaded user

        emailService.sendEmailWithTemplate(user, result, receiver, company).get();
    }
}