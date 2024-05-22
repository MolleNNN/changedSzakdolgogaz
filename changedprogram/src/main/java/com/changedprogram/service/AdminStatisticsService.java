package com.changedprogram.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.changedprogram.entity.QuizAttempt;
import com.changedprogram.repository.QuizAttemptRepository;
import com.changedprogram.repository.UserRepository;

@Service
public class AdminStatisticsService {

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    @Autowired
    private UserRepository userRepository;

 /*   public double calculateAverageAttemptsPerUser() {
        long totalAttempts = quizAttemptRepository.count();
        long totalUsers = userRepository.count();

        if (totalUsers == 0) {
            return 0;
        }

        return (double) totalAttempts / totalUsers;
    }*/
    

    public Map<Integer, Double> getAverageScorePerAttempt() {
        List<Object[]> results = quizAttemptRepository.findAverageScorePerAttempt();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (Integer) result[0], // attempt number
                        result -> (Double) result[1]  // average score
                ));
    }

    public Map<Integer, Long> getCountPerAttempt() {
        List<Object[]> results = quizAttemptRepository.findCountPerAttempt();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (Integer) result[0], // attempt number
                        result -> (Long) result[1]  // count
                ));
    }
    

    public Map<Integer, Long> getCountOfSuccessfulAttempts() {
        List<Object[]> results = quizAttemptRepository.findCountOfSuccessfulAttempts();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (Integer) result[0], // attempt number
                        result -> (Long) result[1]  // count
                ));
    }
}