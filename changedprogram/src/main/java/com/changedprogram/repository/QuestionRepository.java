package com.changedprogram.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.changedprogram.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long>{

    List<Question> findByPptId(Long pptId);
    List<Question> findByPptIdOrderByTextAsc(Long pptId);


    
    @Query("SELECT q FROM Question q WHERE q.ppt.id = :pptId AND q.id NOT IN (SELECT qr.question.id FROM QuizResponse qr)")
    List<Question> findDeletableQuestionsByPptId(@Param("pptId") Long pptId);
    
    @Query("SELECT q FROM Question q WHERE q.ppt.id = :pptId AND LOWER(TRIM(q.text)) = LOWER(TRIM(:normalizedText))")
    List<Question> findByPptIdAndNormalizedText(@Param("pptId") Long pptId, @Param("normalizedText") String normalizedText);

    @Query("SELECT COUNT(q) > 0 FROM Question q WHERE q.ppt.id = :pptId AND q.text = :text AND q.id <> :questionId")
    boolean existsByPptIdAndTextAndNotId(@Param("pptId") Long pptId, @Param("text") String text, @Param("questionId") Long questionId);

}
