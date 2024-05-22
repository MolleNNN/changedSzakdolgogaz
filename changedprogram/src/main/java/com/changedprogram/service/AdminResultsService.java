package com.changedprogram.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.changedprogram.dto.QuizAttemptDetailDTO;
import com.changedprogram.dto.QuizResponseDetailDTO;
import com.changedprogram.dto.UserQuizAttemptDTO;
import com.changedprogram.entity.QuizAttempt;
import com.changedprogram.entity.QuizResponse;
import com.changedprogram.entity.User;
import com.changedprogram.repository.QuizAttemptRepository;
import com.changedprogram.repository.QuizResponseRepository;
import com.changedprogram.repository.UserRepository;

@Service
public class AdminResultsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    

    @Autowired
    private QuizResponseRepository quizResponseRepository;

    public List<UserQuizAttemptDTO> getQuizAttemptsSummary() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserQuizAttemptDTO(
                        user.getId(),
                        user.getName(),
                        quizAttemptRepository.countByUserId(user.getId())
                ))
                .collect(Collectors.toList());
    }

    public List<QuizAttemptDetailDTO> getQuizAttemptDetails(Long userId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserId(userId);
        return attempts.stream()
                .map(attempt -> new QuizAttemptDetailDTO(
                        attempt.getId(), // Add this line
                        attempt.getPpt().getFilename(),
                        attempt.getAttemptNumber(),
                        attempt.getPercentage(),
                        attempt.getTimestamp(),
                        attempt.getPpt().getLanguage().getName(),
                        attempt.getPpt().getType().getName()
                ))
                .collect(Collectors.toList());
    }
    
    public List<QuizResponseDetailDTO> getQuizResponseDetails(Long quizAttemptId) {
        List<QuizResponse> responses = quizResponseRepository.findByQuizAttemptId(quizAttemptId);
        return responses.stream()
                .map(response -> new QuizResponseDetailDTO(
                        response.getQuestion().getText(),
                        response.isResponse(),
                        response.isCorrect() // maps to correct in DTO
                ))
                .collect(Collectors.toList());
    }

}