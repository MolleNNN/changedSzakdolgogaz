package com.changedprogram.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

@Configuration  // Indicates that this class contains Spring configuration settings
public class ThymeleafConfig {

    @Bean  // Defines a bean for SpringResourceTemplateResolver
    public SpringResourceTemplateResolver templateResolver(){
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/templates/");  // Sets the prefix for the template location
        templateResolver.setSuffix(".html");  // Sets the suffix for the template files
        templateResolver.setTemplateMode("HTML");  // Sets the template mode to HTML
        templateResolver.setCharacterEncoding("UTF-8");  // Sets the character encoding to UTF-8
        return templateResolver;  // Returns the template resolver bean
    }

    @Bean  // Defines a bean for SpringTemplateEngine
    public SpringTemplateEngine templateEngine(SpringResourceTemplateResolver templateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);  // Sets the template resolver
        engine.addDialect(new Java8TimeDialect());  // Adds the Java 8 Time dialect
    //    engine.addDialect(sec); // Example of adding Spring Security dialect (commented out)
        return engine;  // Returns the template engine bean
    }

    @Bean  // Defines a bean for ThymeleafViewResolver
    public ThymeleafViewResolver viewResolver(SpringTemplateEngine templateEngine){
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);  // Sets the template engine
        viewResolver.setCharacterEncoding("UTF-8");  // Sets the character encoding to UTF-8
        return viewResolver;  // Returns the view resolver bean
    }
}
