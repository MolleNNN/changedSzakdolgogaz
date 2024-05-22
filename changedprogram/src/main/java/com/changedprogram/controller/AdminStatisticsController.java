package com.changedprogram.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.changedprogram.service.AdminStatisticsService;

@Controller
@RequestMapping("/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private AdminStatisticsService adminStatisticsService;
/*
    @GetMapping
    public String getStatisticsPage(Model model) {
        double averageAttempts = adminStatisticsService.calculateAverageAttemptsPerUser();
        model.addAttribute("averageAttempts", averageAttempts);
        return "statistics";
    }
    */
    @GetMapping
    public String getStatisticsPage(Model model) {
        Map<Integer, Double> averageScores = adminStatisticsService.getAverageScorePerAttempt();
        Map<Integer, Long> attemptCounts = adminStatisticsService.getCountPerAttempt();
        Map<Integer, Long> successfulAttempts = adminStatisticsService.getCountOfSuccessfulAttempts();
        model.addAttribute("averageScores", averageScores);
        model.addAttribute("attemptCounts", attemptCounts);
        model.addAttribute("successfulAttempts", successfulAttempts);
        return "statistics";
    }
}
