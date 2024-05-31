package com.changedprogram.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.changedprogram.entity.Question;
import com.changedprogram.entity.QuizAttempt;
import com.changedprogram.repository.QuestionRepository;
import com.changedprogram.repository.QuizAttemptRepository;
import com.changedprogram.repository.QuizResponseRepository;
import com.changedprogram.repository.UserRepository;

@Service
public class AdminStatisticsService {

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuizResponseRepository quizResponseRepository;

 /*   public double calculateAverageAttemptsPerUser() {
        long totalAttempts = quizAttemptRepository.count();
        long totalUsers = userRepository.count();

        if (totalUsers == 0) {
            return 0;
        }

        return (double) totalAttempts / totalUsers;
    }*/
    

    public List<Map<String, Object>> getAverageScorePerAttempt() {
        List<Object[]> results = quizAttemptRepository.findAverageScorePerAttempt();
        return results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("attemptNumber", result[0]);
                    map.put("averageScore", result[1]);
                    map.put("attemptCount", result[2]);
                    return map;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Long> getAttemptsPerDay() {
        List<Object[]> results = quizAttemptRepository.findAttemptsPerDay();
        return fillMissingDays(results, null, null);
    }

    public Map<String, Long> getAttemptsPerDayForDateRange(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = quizAttemptRepository.findAttemptsPerDayForDateRange(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        return fillMissingDays(results, startDate, endDate);
    }

    private Map<String, Long> fillMissingDays(List<Object[]> results, LocalDate startDate, LocalDate endDate) {
        Map<String, Long> attemptsPerDay = results.stream()
                .collect(Collectors.toMap(
                        result -> ((java.sql.Date) result[0]).toString(), // date
                        result -> (Long) result[1]  // attempt count
                ));

        if (startDate == null || endDate == null) {
            // Default to last 7 days if no specific date range is provided
            startDate = LocalDate.now().minusDays(6);
            endDate = LocalDate.now();
        }

        Map<String, Long> completeAttemptsPerDay = new LinkedHashMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            completeAttemptsPerDay.put(date.toString(), attemptsPerDay.getOrDefault(date.toString(), 0L));
        }
        return completeAttemptsPerDay;
    }
    
    public List<Map<String, Object>> getAverageAndTotalAttemptsPerPpt() {
        List<Object[]> averageResults = quizAttemptRepository.findAverageAttemptsPerPpt();
        List<Object[]> totalResults = quizAttemptRepository.findTotalAttemptsPerPpt();

        Map<String, Double> averageAttemptsMap = averageResults.stream()
            .collect(Collectors.toMap(
                result -> (String) result[0], // PPT Name
                result -> (Double) result[1]  // Average attempt number
            ));

        Map<String, Long> totalAttemptsMap = totalResults.stream()
            .collect(Collectors.toMap(
                result -> (String) result[0], // PPT Name
                result -> (Long) result[1]    // Total attempt number
            ));

        return averageAttemptsMap.keySet().stream()
            .map(pptName -> {
                Map<String, Object> map = new HashMap<>();
                map.put("pptName", pptName);
                map.put("averageAttempts", averageAttemptsMap.get(pptName));
                map.put("totalAttempts", totalAttemptsMap.get(pptName));
                return map;
            })
            .collect(Collectors.toList());
    }
    
    public List<Question> getAllQuestions() {
        return questionRepository.findAllQuestions();
    }
    
    public Map<String, Long> getResponseStatistics(Long questionId) {
        Long correct = quizResponseRepository.countCorrectResponsesByQuestionId(questionId);
        Long incorrect = quizResponseRepository.countIncorrectResponsesByQuestionId(questionId);
        Map<String, Long> stats = new HashMap<>();
        stats.put("correct", correct);
        stats.put("incorrect", incorrect);
        return stats;
    }
}