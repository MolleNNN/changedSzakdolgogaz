package com.changedprogram.component;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component  // Marks this class as a Spring component, enabling Spring to discover it for dependency injection
public class LanguageInterceptor implements HandlerInterceptor {

    @Autowired  // Injects the LocaleResolver bean to manage locale settings
    private LocaleResolver localeResolver;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();  // Retrieve the current HTTP session
        String language = (String) session.getAttribute("sessionLanguage");  // Get the language attribute from the session
        if (language == null) {  // If the language is not set in the session
            language = "hu";  // Set default language to Hungarian
            session.setAttribute("sessionLanguage", language);  // Store the default language in the session
        }
        // Set the locale for the current request and response based on the language
        localeResolver.setLocale(request, response, new Locale(language));
        return true;  // Continue with the request processing
    }
}
