package com.changedprogram.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class LanguageController {

    private static final Logger logger = LoggerFactory.getLogger(LanguageController.class);

    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    
    @GetMapping("/")
    public String welcome(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        Long userId = (Long) session.getAttribute("userId");
        String sessionLanguage = (String) session.getAttribute("sessionLanguage");

        logger.debug("Welcome page accessed. Current session attributes - User ID: {}, Language: {}", userId, sessionLanguage);

        if (sessionLanguage == null) {
            sessionLanguage = "hu"; // Default to English if no language is set
            session.setAttribute("sessionLanguage", sessionLanguage);
        }

        Locale locale = new Locale(sessionLanguage);
        localeResolver.setLocale(request, response, locale);

        logger.info("Displaying welcome page for user ID: {}, Language: {}", userId != null ? userId : "none", sessionLanguage != null ? sessionLanguage : "none");
        logSessionAttributes(session); // Log session attributes
        return "welcome";
    }

    @PostMapping("/change-language")
    public String changeLanguage(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam("lang") String lang, HttpSession session) throws IOException {
        String query = "SELECT code FROM language WHERE code = ?";
        String languageCode = jdbcTemplate.queryForObject(query, new Object[]{lang}, String.class);

        if (languageCode != null) {
            Locale locale = new Locale(languageCode);
            localeResolver.setLocale(request, response, locale);
            session.setAttribute("sessionLanguage", languageCode);  // Store language in session

            logger.info("Language changed to: {}. Session language set to: {}", languageCode, session.getAttribute("sessionLanguage"));
        } else {
            logger.warn("Invalid language code: {}", lang);
        }
        logSessionAttributes(session); // Log session attributes
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