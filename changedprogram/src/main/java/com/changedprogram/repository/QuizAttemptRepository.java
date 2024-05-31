package com.changedprogram.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.changedprogram.entity.QuizAttempt;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    
	@Query("SELECT qa.attemptNumber, AVG(qa.percentage), COUNT(qa) FROM QuizAttempt qa GROUP BY qa.attemptNumber")
	List<Object[]> findAverageScorePerAttempt();
    
    @Query("SELECT DATE(qa.timestamp), COUNT(qa) FROM QuizAttempt qa GROUP BY DATE(qa.timestamp)")
    List<Object[]> findAttemptsPerDay();

    @Query("SELECT DATE(qa.timestamp), COUNT(qa) FROM QuizAttempt qa WHERE qa.timestamp BETWEEN :startDate AND :endDate GROUP BY DATE(qa.timestamp)")
    List<Object[]> findAttemptsPerDayForDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    List<QuizAttempt> findByUserId(Long userId); // Method to fetch quiz attempts by user ID

    long countByUserId(Long userId);

    @Query("SELECT p.filename, AVG(qa.attemptNumber) FROM QuizAttempt qa JOIN qa.ppt p WHERE qa.percentage >= 90 GROUP BY p.filename")
    List<Object[]> findAverageAttemptsPerPpt();

    @Query("SELECT p.filename, COUNT(qa) FROM QuizAttempt qa JOIN qa.ppt p GROUP BY p.filename")
    List<Object[]> findTotalAttemptsPerPpt();

}
