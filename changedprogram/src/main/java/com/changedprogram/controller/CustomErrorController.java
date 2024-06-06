package com.changedprogram.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        String errorMessage = "Váratlan hiba történt!";
        String errorTitle = "Error";

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorTitle = "404 - Az oldal nem található";
                errorMessage = "Lehet, hogy a keresett oldalt eltávolították, megváltoztatták a nevét, vagy átmenetileg nem elérhető.";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorTitle = "500 - Belső kiszolgáló hiba";
                errorMessage = "Váratlan hiba történt a szerveren. Kérjük, próbálja meg később újra.";
            } else {
                errorTitle = statusCode + " - Váratlan hiba";
                errorMessage = "Váratlan hiba történt.";
            }

            model.addAttribute("errorCode", statusCode);
        } else {
            model.addAttribute("errorCode", "Unknown");
        }

        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorMessage", errorMessage);

        return "error";
    }
}