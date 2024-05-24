package com.changedprogram.controller;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.changedprogram.entity.Ppt;
import com.changedprogram.entity.Question;
import com.changedprogram.entity.QuizAttempt;
import com.changedprogram.entity.User;
import com.changedprogram.repository.PptRepository;
import com.changedprogram.repository.QuestionRepository;
import com.changedprogram.repository.QuizAttemptRepository;
import com.changedprogram.repository.UserRepository;
import com.changedprogram.service.QuizService;

import jakarta.servlet.http.HttpSession;

@Controller
public class QuizController {

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    private final QuizService quizService;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final PptRepository pptRepository;

    @Autowired
    public QuizController(QuizService quizService, QuestionRepository questionRepository, UserRepository userRepository, PptRepository pptRepository) {
        this.quizService = quizService;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.pptRepository = pptRepository;
    }

    @GetMapping("/quiz")
    public synchronized String startQuiz(HttpSession session, Model model) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            Long pptId = (Long) session.getAttribute("pptId");

            logger.info("Starting quiz for user ID: {}, pptId: {}", userId, pptId);
            logSessionAttributes(session);

            if (pptId == null) {
                model.addAttribute("message", "Presentation ID is missing from the session.");
                return "error";
            }

            List<Question> questions = questionRepository.findByPptId(pptId);
            if (questions == null || questions.isEmpty()) {
                model.addAttribute("message", "No questions available for this presentation.");
                return "error";
            }

            // Shuffle the questions to randomize their order
            Collections.shuffle(questions);

            // Clear previous quiz data from the session
            clearPreviousQuizData(session);

            session.setAttribute("questions", questions);
            session.setAttribute("currentQuestionIndex", 0);
            session.setAttribute("correctAnswersCount", 0);

            // Increment attempt number
            Integer attemptNumber = (Integer) session.getAttribute("attemptNumber");
            if (attemptNumber == null) {
                attemptNumber = 1;
            } else {
                attemptNumber++;
            }
            session.setAttribute("attemptNumber", attemptNumber);

            // Create a new quiz attempt
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            Ppt ppt = pptRepository.findById(pptId).orElseThrow(() -> new RuntimeException("Ppt not found"));
            QuizAttempt quizAttempt = quizService.logQuizAttempt(user, ppt, 0, attemptNumber); // Initially, the score is 0
            session.setAttribute("quizAttemptId", quizAttempt.getId());

            double progressPercentage = (1.0 / questions.size()) * 100;

            model.addAttribute("question", questions.get(0));
            model.addAttribute("currentQuestionIndex", 1);
            model.addAttribute("totalQuestions", questions.size());
            model.addAttribute("progressPercentage", progressPercentage);

            logSessionAttributes(session);
            return "quiz";
        } catch (Exception e) {
            logger.error("Error starting quiz", e);
            model.addAttribute("message", "An error occurred while starting the quiz. Please try again.");
            return "error";
        }
    }


    @PostMapping("/nextQuestion")
    @ResponseBody
    public synchronized ResponseEntity<?> processQuestion(@RequestParam("answer") boolean answer, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            Long quizAttemptId = (Long) session.getAttribute("quizAttemptId");
            logger.info("Processing next question for user ID: {}", userId);

            if (!isSessionValid(session)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Session not initialized");
            }

            List<Question> questions = (List<Question>) session.getAttribute("questions");
            int currentIndex = (Integer) session.getAttribute("currentQuestionIndex");
            int correctAnswersCount = (Integer) session.getAttribute("correctAnswersCount");

            if (questions == null || currentIndex >= questions.size()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No more questions available");
            }

            Question currentQuestion = questions.get(currentIndex);
            boolean isCorrect = currentQuestion.isAnswer() == answer;
            if (isCorrect) {
                correctAnswersCount++;
                session.setAttribute("correctAnswersCount", correctAnswersCount);
            }

            // Log the quiz response
            QuizAttempt quizAttempt = quizAttemptRepository.findById(quizAttemptId).orElseThrow(() -> new RuntimeException("Quiz attempt not found"));
            quizService.logQuizResponse(quizAttempt, currentQuestion, answer, isCorrect);

            currentIndex++;
            session.setAttribute("currentQuestionIndex", currentIndex);

            if (currentIndex >= questions.size()) {
                double scorePercentage = ((double) correctAnswersCount / questions.size()) * 100;
                session.setAttribute("scorePercentage", scorePercentage);

                // Update the quiz attempt with the final score
                try {
                    quizAttempt.setPercentage((int) scorePercentage); // Save percentage
                    quizAttemptRepository.save(quizAttempt);
                } catch (Exception e) {
                    logger.error("Error updating quiz attempt", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update quiz attempt.");
                }

                // Determine if the user passed or failed
                boolean passed = quizService.checkQuizResults(questions, correctAnswersCount);
                session.setAttribute("passed", passed);

                return ResponseEntity.ok(Map.of("completed", true, "redirect", "/summarize"));
            } else {
                Question nextQuestion = questions.get(currentIndex);
                double progressPercentage = ((double) (currentIndex + 1) / questions.size()) * 100; // Calculate progress

                return ResponseEntity.ok(Map.of(
                    "questionText", nextQuestion.getText(),
                    "questionId", nextQuestion.getId(),
                    "completed", false,
                    "currentQuestionIndex", currentIndex + 1, // Send 1-based index for display
                    "totalQuestions", questions.size(),
                    "progressPercentage", progressPercentage // Include progress percentage
                ));
            }
        } catch (Exception e) {
            logger.error("Error processing question", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the question. Please try again.");
        }
    }


    @PostMapping("/processQuiz")
    public synchronized String processQuiz(@RequestParam Map<String, String> allParams, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            Long pptId = (Long) session.getAttribute("pptId");
            Long quizAttemptId = (Long) session.getAttribute("quizAttemptId");
            logger.info("Processing quiz for user ID: {} and PPT ID: {}", userId, pptId);

            if (userId == null || pptId == null || quizAttemptId == null) {
                redirectAttributes.addFlashAttribute("error", "Session data missing.");
                return "redirect:/error";
            }

            List<Question> questions = (List<Question>) session.getAttribute("questions");
            Integer correctAnswersCount = (Integer) session.getAttribute("correctAnswersCount");

            if (questions == null || correctAnswersCount == null) {
                redirectAttributes.addFlashAttribute("error", "Session data missing.");
                return "redirect:/error";
            }

            double scorePercentage = ((double) correctAnswersCount / questions.size()) * 100;
            boolean passed = quizService.checkQuizResults(questions, correctAnswersCount);

            // Update the quiz attempt with the final score
            QuizAttempt quizAttempt = quizAttemptRepository.findById(quizAttemptId).orElseThrow(() -> new RuntimeException("Quiz attempt not found"));
            quizAttempt.setPercentage((int) scorePercentage); // Save percentage
            quizAttemptRepository.save(quizAttempt);

            // Store results in session for summarization
            session.setAttribute("scorePercentage", scorePercentage);
            session.setAttribute("passed", passed);

            // Redirect to summarize page
            return "redirect:/summarize";
        } catch (Exception e) {
            logger.error("Error processing quiz", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred while processing the quiz. Please try again.");
            return "redirect:/error";
        }
    }

    @GetMapping("/summarize")
    public String summarizeQuiz(HttpSession session, Model model) {
        try {
            Integer correctAnswersCount = (Integer) session.getAttribute("correctAnswersCount");
            List<Question> questions = (List<Question>) session.getAttribute("questions");
            Double scorePercentage = (Double) session.getAttribute("scorePercentage");
            Boolean passed = (Boolean) session.getAttribute("passed");

            if (questions == null || correctAnswersCount == null || passed == null || scorePercentage == null) {
                model.addAttribute("message", "Session data missing.");
                return "error";
            }

            model.addAttribute("scorePercentage", scorePercentage);
            model.addAttribute("totalQuestions", questions.size());
            model.addAttribute("correctAnswersCount", correctAnswersCount);
            model.addAttribute("passed", passed);

            return "summarize";
        } catch (Exception e) {
            logger.error("Error summarizing quiz", e);
            model.addAttribute("message", "An error occurred while summarizing the quiz. Please try again.");
            return "error";
        }
    }

    private boolean isSessionValid(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Long pptId = (Long) session.getAttribute("pptId");
        return userId != null && pptId != null;
    }

    private void clearPreviousQuizData(HttpSession session) {
        session.removeAttribute("questions");
        session.removeAttribute("currentQuestionIndex");
        session.removeAttribute("correctAnswersCount");
        session.removeAttribute("scorePercentage");
        session.removeAttribute("passed");
    }

    public void logSessionAttributes(HttpSession session) {
        logger.info("Session ID: {}", session.getId());
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = session.getAttribute(attributeName);
            logger.info("Session attribute - {}: {}", attributeName, attributeValue);
        }
    }
}
