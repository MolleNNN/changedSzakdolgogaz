package com.changedprogram;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LanguageInterceptor implements HandlerInterceptor {

    @Autowired
    private LocaleResolver localeResolver;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String language = (String) session.getAttribute("sessionLanguage");
        if (language == null) {
            language = "en"; // Set default language to English
            session.setAttribute("sessionLanguage", language);
        }
        localeResolver.setLocale(request, response, new Locale(language));
        return true;
    }
}