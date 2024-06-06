package com.changedprogram.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.changedprogram.dto.QuizAttemptDetailDTO;
import com.changedprogram.dto.QuizResponseDetailDTO;
import com.changedprogram.dto.UserQuizAttemptDTO;
import com.changedprogram.service.AdminResultsService;

@Controller
@RequestMapping("/admin/results")
public class AdminResultsController {

    @Autowired
    private AdminResultsService adminResultsService;

    @GetMapping
    public String getResultsPage() {
        return "results";
    }

    @GetMapping("/api/quiz-attempts")
    @ResponseBody
    public List<UserQuizAttemptDTO> getQuizAttempts() {
        return adminResultsService.getQuizAttemptsSummary();
    }

    @GetMapping("/api/quiz-attempts/{userId}/details")
    @ResponseBody
    public List<QuizAttemptDetailDTO> getQuizAttemptDetails(@PathVariable Long userId) {
        return adminResultsService.getQuizAttemptDetails(userId);
    }
    
    @GetMapping("/api/quiz-attempts/{quizAttemptId}/responses")
    @ResponseBody
    public List<QuizResponseDetailDTO> getQuizResponseDetails(@PathVariable Long quizAttemptId) {
        return adminResultsService.getQuizResponseDetails(quizAttemptId);
    }
}