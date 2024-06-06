package com.changedprogram.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.changedprogram.component.LanguageInterceptor;


@Configuration  // Indicates that this class contains Spring configuration settings
public class LocaleConfig implements WebMvcConfigurer {
    
    @Bean  // Defines a bean for LocaleResolver
    public LocaleResolver localeResolver() {
        // Create a SessionLocaleResolver to manage locale settings in the session
        SessionLocaleResolver slr = new SessionLocaleResolver();
        return slr;  // Return the session locale resolver bean
    }

    @Bean  // Defines a bean for LocaleChangeInterceptor
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        // Set the parameter name to change the language
        lci.setParamName("lang");
        return lci;  // Return the locale change interceptor bean
    }

    @Bean  // Defines a bean for LanguageInterceptor
    public LanguageInterceptor languageInterceptor() {
        return new LanguageInterceptor();  // Return a new instance of LanguageInterceptor
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register the locale change interceptor in the registry
        registry.addInterceptor(localeChangeInterceptor());
        // Optionally register the custom language interceptor in the registry
        // registry.addInterceptor(languageInterceptor());
    }
}
