package com.changedprogram.controller;

import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.changedprogram.EncryptionUtil;
import com.changedprogram.repository.PptRepository;
import com.changedprogram.repository.ResultRepository;
import com.changedprogram.repository.UserRepository;
import com.changedprogram.service.SignatureService;

import jakarta.servlet.http.HttpSession;

@Controller
public class SignatureController {

    private static final Logger logger = LoggerFactory.getLogger(SignatureController.class);

    @Autowired
    private SignatureService signatureService;

    @GetMapping("/signature")
    public String showSignaturePage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        logger.info("Displaying signature page for user ID: {}", userId);

        logSessionAttributes(session); // Log session attributes before clearing
        
        // Clear quiz-related session attributes
        session.removeAttribute("questions");
        session.removeAttribute("currentQuestionIndex");
        session.removeAttribute("correctAnswersCount");
        session.removeAttribute("scorePercentage");
        session.removeAttribute("passed");
        session.removeAttribute("quizAttemptId");
        session.removeAttribute("attemptNumber"); // Clear the attempt number

        logSessionAttributes(session); // Log session attributes after clearing

        
        model.addAttribute("message", "Congratulations on passing the quiz! Please sign below.");
        return "signature";
    }

    @PostMapping("/finalizeSignature")
    public synchronized String finalizeSignature(@RequestParam("signature") String signatureData, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        Long pptId = (Long) session.getAttribute("pptId");
        logger.info("Finalizing signature for user ID: {} and PPT ID: {}", userId, pptId);

        if (userId == null || pptId == null) {
            redirectAttributes.addFlashAttribute("error", "Session data missing.");
            logSessionAttributes(session); // Log session attributes
            return "redirect:/error";
        }

        return signatureService.finalizeSignature(userId, pptId, signatureData, session, redirectAttributes);
    }
    
    @GetMapping("/completion")
    public String showCompletionPage(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        logger.info("Displaying completion page for user ID: {}", userId);
        logSessionAttributes(session); // Log session attributes
        return "completion";
    }
    
    @GetMapping("/invalidateSession")
    public String invalidateSession(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    public void logSessionAttributes(HttpSession session) {
        logger.info("Session ID: {}", session.getId());
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = session.getAttribute(attributeName);
            logger.info("Session attribute - {}: {}", attributeName, attributeValue);
        }
    }

}
