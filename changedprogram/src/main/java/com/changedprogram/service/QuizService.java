package com.changedprogram.service;


import java.time.LocalDateTime;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.ui.Model;


import com.changedprogram.entity.Ppt;
import com.changedprogram.entity.Question;
import com.changedprogram.entity.QuizAttempt;
import com.changedprogram.entity.QuizResponse;

import com.changedprogram.entity.User;

import com.changedprogram.repository.QuestionRepository;
import com.changedprogram.repository.QuizAttemptRepository;
import com.changedprogram.repository.QuizResponseRepository;



import jakarta.servlet.http.HttpSession;

@Service
public class QuizService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizResponseRepository quizResponseRepository;

    @Autowired
    public QuizService(QuizAttemptRepository quizAttemptRepository, QuizResponseRepository quizResponseRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizResponseRepository = quizResponseRepository;
    }

    @Autowired
    private QuestionRepository questionRepository;


    public List<Question> getQuestionsByPptId(Long pptId) {
        return questionRepository.findByPptId(pptId);
    }


    public QuizAttempt logQuizAttempt(User user, Ppt ppt, int percentage, int attemptNumber) {
        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setUser(user);
        quizAttempt.setPpt(ppt);
        quizAttempt.setAttemptNumber(attemptNumber);
        quizAttempt.setPercentage(percentage); // Save percentage
        quizAttempt.setTimestamp(LocalDateTime.now());
        return quizAttemptRepository.save(quizAttempt);
    }





    public QuizResponse logQuizResponse(QuizAttempt quizAttempt, Question question, boolean response, boolean isCorrect) {
        QuizResponse quizResponse = new QuizResponse();
        quizResponse.setQuizAttempt(quizAttempt);
        quizResponse.setQuestion(question);
        quizResponse.setResponse(response);
        quizResponse.setCorrect(isCorrect);
        return quizResponseRepository.save(quizResponse);
    }

    public boolean checkQuizResults(List<Question> questions, int correctAnswersCount) {
        double scorePercentage = ((double) correctAnswersCount / questions.size()) * 100;
        return scorePercentage >= 90;
    }

    public void prepareQuizModel(Long pptId, HttpSession session, Model model) {
        List<Question> questions = questionRepository.findByPptId(pptId);
        session.setAttribute("questions", questions);
        session.setAttribute("currentQuestionIndex", 0);
        session.setAttribute("correctAnswersCount", 0);
        if (!questions.isEmpty()) {
            model.addAttribute("question", questions.get(0));
            model.addAttribute("totalQuestions", questions.size());
        }
    }
}