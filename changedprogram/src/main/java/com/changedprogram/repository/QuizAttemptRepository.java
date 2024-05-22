package com.changedprogram.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.changedprogram.entity.QuizAttempt;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    
    @Query("SELECT qa.attemptNumber, AVG(qa.percentage) FROM QuizAttempt qa GROUP BY qa.attemptNumber")
    List<Object[]> findAverageScorePerAttempt();
    
    @Query("SELECT qa.attemptNumber, COUNT(qa) FROM QuizAttempt qa GROUP BY qa.attemptNumber")
    List<Object[]> findCountPerAttempt();
    
    @Query("SELECT qa.attemptNumber, COUNT(qa) FROM QuizAttempt qa WHERE qa.percentage >= 50 GROUP BY qa.attemptNumber")
    List<Object[]> findCountOfSuccessfulAttempts();

    List<QuizAttempt> findByUserId(Long userId); // Method to fetch quiz attempts by user ID

    long countByUserId(Long userId);

}
