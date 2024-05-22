package com.changedprogram.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.changedprogram.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long>{

    List<Question> findByPptId(Long pptId);

}
