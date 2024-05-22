package com.changedprogram.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.changedprogram.entity.QuizResponse;

public interface QuizResponseRepository extends JpaRepository<QuizResponse, Long> {
    List<QuizResponse> findByQuizAttemptId(Long quizAttemptId);

}