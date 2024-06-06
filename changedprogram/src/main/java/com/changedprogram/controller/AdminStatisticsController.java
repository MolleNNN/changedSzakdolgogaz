package com.changedprogram.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.changedprogram.entity.Question;
import com.changedprogram.service.AdminStatisticsService;

@Controller
@RequestMapping("/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private AdminStatisticsService adminStatisticsService;

    @GetMapping
    public String getStatisticsPage(Model model) {
        List<Map<String, Object>> averageScores = adminStatisticsService.getAverageScorePerAttempt();
        Map<String, Long> attemptsPerDay = adminStatisticsService.getAttemptsPerDay();
        model.addAttribute("averageScores", averageScores);
        model.addAttribute("attemptsPerDay", attemptsPerDay);
        return "statistics";
    }

    @GetMapping("/averageAndTotalAttemptsPerPpt")
    @ResponseBody
    public List<Map<String, Object>> getAverageAndTotalAttemptsPerPpt() {
        return adminStatisticsService.getAverageAndTotalAttemptsPerPpt();
    }
    
    @GetMapping("/questions")
    @ResponseBody
    public List<Question> getAllQuestions() {
        return adminStatisticsService.getAllQuestions();
    }

    @GetMapping("/questionResponses/{questionId}")
    @ResponseBody
    public Map<String, Long> getResponseStatistics(@PathVariable Long questionId) {
        return adminStatisticsService.getResponseStatistics(questionId);
    }
    @PostMapping("/dateRange")
    @ResponseBody
    public Map<String, Object> getStatisticsForDateRange(@RequestParam String startDate, @RequestParam String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        Map<String, Long> attemptsPerDay = adminStatisticsService.getAttemptsPerDayForDateRange(start, end);
        Map<String, Object> response = new HashMap<>();
        response.put("attemptsPerDay", attemptsPerDay);
        return response;
    }
}