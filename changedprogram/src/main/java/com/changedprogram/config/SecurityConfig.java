package com.changedprogram.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;


@Configuration  // Indicates that this class contains Spring configuration settings
@EnableWebSecurity  // Enables web security configuration using Spring Security
public class SecurityConfig {

    @Value("${admin.username}")  // Injects the admin username from the application properties file
    private String adminUsername;

    @Value("${admin.password}")  // Injects the admin password from the application properties file
    private String adminPassword;

    @Bean  // Defines a bean for password encoding
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Returns a BCryptPasswordEncoder instance
    }

    @Bean  // Defines a bean for UserDetailsService
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username(adminUsername)  // Sets the admin username
                .password(passwordEncoder().encode(adminPassword))  // Encrypts and sets the admin password
                .roles("ADMIN")  // Assigns the "ADMIN" role
                .build();
        return new InMemoryUserDetailsManager(user);  // Returns an InMemoryUserDetailsManager instance containing the admin user
    }

    @Bean  // Defines a bean for SecurityFilterChain
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))  // Enables CSRF protection and sets the cookie-based CSRF token repository
            .authorizeRequests(authorize -> authorize
                .requestMatchers("/login", "/").permitAll()  // Explicitly allows access to the root and login pages without authentication
                .requestMatchers("/admin/**").hasRole("ADMIN")  // Requires the ADMIN role for /admin/** paths
                .anyRequest().permitAll()  // Allows all other requests without authentication
            )
            .formLogin(form -> form
                .loginPage("/login")  // Sets the login page
                .defaultSuccessUrl("/admin", true)  // Sets the default success page after login
                .permitAll()  // Allows access to the login page without authentication
            )
            .logout(logout -> logout.permitAll())  // Allows access to the logout functionality without authentication
            .sessionManagement(session -> session
                .sessionFixation().none()  // Prevents Spring Security from creating a new session
            );
        return http.build();  // Builds and returns the SecurityFilterChain instance
    }

    @Bean  // Defines a bean for HttpSessionEventPublisher
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();  // Returns an HttpSessionEventPublisher instance
    }
}
