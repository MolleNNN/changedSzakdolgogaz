package com.changedprogram.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        String errorMessage = "Unexpected error";
        String errorTitle = "Error";

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorTitle = "404 - Page Not Found";
                errorMessage = "The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorTitle = "500 - Internal Server Error";
                errorMessage = "There was an unexpected error on the server. Please try again later.";
            } else {
                errorTitle = statusCode + " - Unexpected Error";
                errorMessage = "An unexpected error has occurred.";
            }

            model.addAttribute("errorCode", statusCode);
        } else {
            model.addAttribute("errorCode", "Unknown");
        }

        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorMessage", errorMessage);

        // Return the template name without specifying a subdirectory
        return "error";
    }
}