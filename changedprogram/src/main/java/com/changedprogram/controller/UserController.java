package com.changedprogram.controller;

import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.LocaleResolver;

import com.changedprogram.entity.Ppt;
import com.changedprogram.entity.UserFormModel;
import com.changedprogram.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {
	
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	
    @Autowired
    private UserService userService;

    @Autowired
    private LocaleResolver localeResolver;

    @ModelAttribute("userForm")
    public UserFormModel userFormModel() {
        return new UserFormModel();
    }

    @GetMapping("/add")
    public String showUserForm(Model model,
                               @RequestParam(value = "lang", required = false) String lang,
                               HttpSession session,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        Long userId = (Long) session.getAttribute("userId");
        String sessionLanguage = (String) session.getAttribute("sessionLanguage");

        logger.debug("Received 'lang' parameter: {}", lang);
        logger.debug("Current session language before processing: {}", sessionLanguage);

        if (lang != null) {
            sessionLanguage = lang;
            session.setAttribute("sessionLanguage", sessionLanguage);
            logger.debug("Updated session language based on 'lang' parameter: {}", sessionLanguage);
        }

        if (sessionLanguage == null) {
            sessionLanguage = "hu"; // Default to English if no language is set
            session.setAttribute("sessionLanguage", sessionLanguage);
            logger.debug("No session language set. Defaulting to: {}", sessionLanguage);
        }

        Locale locale = new Locale(sessionLanguage);
        localeResolver.setLocale(request, response, locale);
        logger.debug("Locale set to: {}", locale);

        logger.info("Displaying add user form. Language: {}, User ID: {}", sessionLanguage, userId != null ? userId : "none");

        userService.prepareFormModel(model);
        logSessionAttributes(session); // Log session attributes
        return "addUser";
    }
    
    @PostMapping("/add")
    public String processUserForm(
        @Valid @ModelAttribute("userForm") UserFormModel userForm,
        BindingResult result,
        Model model,
        HttpSession session
    ) {
        logger.debug("Entering processForm method with userForm: {}", userForm);

        // Delegate validation to the service
        userService.validateUserForm(userForm, result);

        if (result.hasErrors()) {
            logger.debug("Validation errors found: {}", result.getAllErrors());
            userService.prepareFormModel(model);
            logSessionAttributes(session); // Log session attributes
            return "addUser";
        }

        Long userId = userService.registerOrUpdateUser(userForm);
        session.setAttribute("userId", userId);
        model.addAttribute("userId", userId);

        String language = (String) session.getAttribute("sessionLanguage");
        logger.info("User saved with ID: {}. Current Language: {}", userId, language != null ? language : "none");
        logSessionAttributes(session); // Log session attributes
        return "redirect:/options";
    }
    
    @GetMapping("/options")
    public String showAvailablePresentations(Model model, HttpSession session) {
        String language = (String) session.getAttribute("sessionLanguage");
        Long userId = (Long) session.getAttribute("userId");

        logger.info("Displaying options for user ID: {}, Language: {}", userId != null ? userId : "none", language);

        if (language == null) {
            language = "hu"; // Default to English if no language is set
            session.setAttribute("sessionLanguage", language);
        }

        
        List<Ppt> activePresentations = userService.getActivePresentationsByLanguage(language);
        logger.debug("Active presentations found: {}", activePresentations.size());
        if (activePresentations.isEmpty()) {
            model.addAttribute("message", "No available presentations at the moment.");
        } else {
            model.addAttribute("presentations", activePresentations);
        }
        logSessionAttributes(session); // Log session attributes
        return "options";
    }
    
    @PostMapping("/presentation")
    public String showPresentation(@RequestParam("pptId") Long pptId, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String language = (String) session.getAttribute("sessionLanguage");

        // Log user ID, pptId, and language
        logger.info("Displaying presentation for user ID: {}", userId != null ? userId : "none");
        logger.debug("Presentation ID (pptId): {}", pptId);
        logger.debug("Session Language: {}", language != null ? language : "none");

        // Store pptId in session
        session.setAttribute("pptId", pptId);
        logSessionAttributes(session); // Log session attributes
        return userService.preparePresentation(pptId, model, session);
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