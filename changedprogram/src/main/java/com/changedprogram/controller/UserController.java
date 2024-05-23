package com.changedprogram.controller;

import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    
    @Autowired
    private MessageSource messageSource;

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
            sessionLanguage = "hu"; // Default to Hungarian if no language is set
            session.setAttribute("sessionLanguage", sessionLanguage);
            logger.debug("No session language set. Defaulting to: {}", sessionLanguage);
        }

        Locale locale = new Locale(sessionLanguage);
        localeResolver.setLocale(request, response, locale);
        logger.debug("Locale set to: {}", locale);

        logger.info("Displaying add user form. Language: {}, User ID: {}", sessionLanguage, userId != null ? userId : "none");

        userService.prepareFormModel(model);
        logSessionAttributes(session);
        return "addUser";
    }

    @PostMapping("/add")
    public String processUserForm(
        @Valid @ModelAttribute("userForm") UserFormModel userForm,
        BindingResult result,
        Model model,
        HttpSession session,
        RedirectAttributes redirectAttributes,
        Locale locale
    ) {
        logger.debug("Entering processForm method with userForm: {}", userForm);

        if (result.hasErrors()) {
            logger.debug("Validation errors found: {}", result.getAllErrors());
            userService.prepareFormModel(model);
            logSessionAttributes(session);
            return "addUser";
        }

        try {
            Long userId = userService.registerOrUpdateUser(userForm);
            session.setAttribute("userId", userId);
            redirectAttributes.addFlashAttribute("userId", userId);
            redirectAttributes.addFlashAttribute("successMessage", messageSource.getMessage("success.user.registered", null, locale));

            String language = (String) session.getAttribute("sessionLanguage");
            logger.info("User saved with ID: {}. Current Language: {}", userId, language != null ? language : "none");
            logSessionAttributes(session);
            return "redirect:/options";
        } catch (Exception e) {
            logger.error("Error occurred during user registration: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("error.unexpected", null, locale));
            return "redirect:/add";
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception ex, Model model) {
        logger.error("Request: " + request.getRequestURL() + " raised " + ex);

        model.addAttribute("errorMessage", messageSource.getMessage("error.unexpected", null, request.getLocale()));
        return "error";
    }
    
    @GetMapping("/options")
    public String showAvailablePresentations(Model model, HttpSession session) {
        try {
            String language = (String) session.getAttribute("sessionLanguage");
            Long userId = (Long) session.getAttribute("userId");

            logger.info("Displaying options for user ID: {}, Language: {}", userId != null ? userId : "none", language);

            if (language == null) {
                language = "hu"; // Default to Hungarian if no language is set
                session.setAttribute("sessionLanguage", language);
            }

            List<Ppt> activePresentations = userService.getActivePresentationsByLanguage(language);
            logger.debug("Active presentations found: {}", activePresentations.size());
            if (activePresentations.isEmpty()) {
                model.addAttribute("message", messageSource.getMessage("options.message.noTraining", null, localeResolver.resolveLocale(null)));
            } else {
                model.addAttribute("presentations", activePresentations);
            }
            logSessionAttributes(session);
            return "options";
        } catch (Exception e) {
            logger.error("Error occurred while showing available presentations: ", e);
            model.addAttribute("errorMessage", messageSource.getMessage("error.unexpected", null, localeResolver.resolveLocale(null)));
            return "error";
        }
    }

    @PostMapping("/presentation")
    public String showPresentation(@RequestParam("pptId") Long pptId, Model model, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            String language = (String) session.getAttribute("sessionLanguage");

            logger.info("Displaying presentation for user ID: {}", userId != null ? userId : "none");
            logger.debug("Presentation ID (pptId): {}", pptId);
            logger.debug("Session Language: {}", language != null ? language : "none");

            session.setAttribute("pptId", pptId);
            logSessionAttributes(session);
            return userService.preparePresentation(pptId, model, session);
        } catch (Exception e) {
            logger.error("Error occurred while preparing presentation: ", e);
            model.addAttribute("errorMessage", messageSource.getMessage("error.unexpected", null, localeResolver.resolveLocale(null)));
            return "error";
        }
    }

    private void logSessionAttributes(HttpSession session) {
        logger.info("Session ID: {}", session.getId());
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = session.getAttribute(attributeName);
            logger.info("Session attribute - {}: {}", attributeName, attributeValue);
        }
    }
}