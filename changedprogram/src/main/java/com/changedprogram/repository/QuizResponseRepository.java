package com.changedprogram.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.changedprogram.entity.QuizResponse;

public interface QuizResponseRepository extends JpaRepository<QuizResponse, Long> {
    List<QuizResponse> findByQuizAttemptId(Long quizAttemptId);

    
    @Query("SELECT COUNT(qr) FROM QuizResponse qr WHERE qr.question.id = :questionId AND qr.isCorrect = true")
    Long countCorrectResponsesByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT COUNT(qr) FROM QuizResponse qr WHERE qr.question.id = :questionId AND qr.isCorrect = false")
    Long countIncorrectResponsesByQuestionId(@Param("questionId") Long questionId);
}