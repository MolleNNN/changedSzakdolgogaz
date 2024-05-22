package com.changedprogram.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.changedprogram.entity.Image;
import com.changedprogram.entity.Language;
import com.changedprogram.entity.Ppt;
import com.changedprogram.entity.Question;
import com.changedprogram.entity.Type;
import com.changedprogram.repository.ImageRepository;
import com.changedprogram.repository.LanguageRepository;
import com.changedprogram.repository.PptRepository;
import com.changedprogram.repository.QuestionRepository;
import com.changedprogram.repository.TypeRepository;
import com.changedprogram.service.AdminService;
import com.changedprogram.service.FileService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolationException;

@Controller
public class AdminPptController {

    @Autowired
    private PptRepository pptRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private FileService fileService;
    
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private AdminService adminService;

	
	
    @GetMapping("/admin/ppt")
    public String pptPage(Model model) {
        List<Ppt> activePpts = pptRepository.findByIsActiveTrue();
        List<Ppt> allPpts = pptRepository.findAll();
        model.addAttribute("activePpts", activePpts);
        model.addAttribute("allPpts", allPpts);
        return "ppt";
    }
    
    @GetMapping("/admin/getPptImages")
    @ResponseBody
    public List<String> getPptImages(@RequestParam Long pptId) {
        List<Image> images = imageRepository.findByPptId(pptId);
        return images.stream()
            .map(image -> Base64.getEncoder().encodeToString(image.getImage()))
            .collect(Collectors.toList());
    }
    
    @PostMapping("/admin/setPptSession")
    public String setPptSession(@RequestParam Long pptId, HttpSession session) {
        session.setAttribute("pptId", pptId);
        return "redirect:/admin/addquestions";
    }
    
    @GetMapping("/admin/addquestions")
    public String addQuestionsPage(HttpSession session, Model model) {
        Long pptId = (Long) session.getAttribute("pptId");
        if (pptId == null) {
            return "redirect:/admin/ppt"; // Redirect if pptId is not found in session
        }
        Ppt ppt = pptRepository.findById(pptId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid PPT ID: " + pptId));
        List<Question> questions = questionRepository.findByPptId(pptId);
        model.addAttribute("pptId", pptId);
        model.addAttribute("pptName", ppt.getFilename());
        model.addAttribute("questions", questions);
        return "addquestions";
    }

    @PostMapping("/admin/addquestion")
    public String addQuestion(@RequestParam String question,
                              @RequestParam boolean answer,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Long pptId = (Long) session.getAttribute("pptId");
        if (pptId == null) {
            return "redirect:/admin/ppt"; // Redirect if pptId is not found in session
        }
        try {
            fileService.addQuestion(pptId, question, answer);
            redirectAttributes.addFlashAttribute("message", "Question added successfully!");
        } catch (ConstraintViolationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/addquestions";
    }

    @GetMapping("/admin/new")
    public String newPptPage(Model model) {
        List<Language> languages = languageRepository.findAll();
        List<Type> types = typeRepository.findAll();
        model.addAttribute("languages", languages);
        model.addAttribute("types", types);
        return "newPpt";
    }
    
    @PostMapping("/uploadPpt")
    public String handleFileUpload(@RequestParam("name") String name,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestParam("languageCode") String languageCode,
                                   @RequestParam("typeId") Long typeId,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        try {
            Ppt ppt = fileService.storePpt(name, file, languageCode, typeId);
            session.setAttribute("pptId", ppt.getId());  // Store pptId in session
            redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + name + "!");
            return "redirect:/admin/addquestions";  // Redirect to add questions page
        } catch (IOException | InvalidFormatException e) {
            redirectAttributes.addFlashAttribute("message", "Failed to upload " + name + ": " + e.getMessage());
            return "redirect:/admin/new";
        }
    }

    @PostMapping("/admin/changeActivePpt")
    public String changeActivePpt(@RequestParam List<Long> pptIds) {
        adminService.setActivePpts(pptIds);
        return "redirect:/admin/ppt";
    }
    
    @PostMapping("/admin/modifyQuestion")
    @Transactional
    public ResponseEntity<?> modifyQuestion(
            @RequestParam("questionId") Long questionId,
            @RequestParam("question") String questionText,
            @RequestParam("answer") boolean answer) {
        try {
            // Find the existing question
            Question existingQuestion = questionRepository.findById(questionId)
                    .orElseThrow(() -> new IllegalStateException("Question not found with ID: " + questionId));

            // Update the question
            existingQuestion.setText(questionText);
            existingQuestion.setAnswer(answer);

            // Save the updated question
            questionRepository.save(existingQuestion);

            // Return a successful response
            return ResponseEntity.ok("Question updated successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Generic exception handling
            return ResponseEntity.badRequest().body("Failed to update the question: " + e.getMessage());
        }
    }

    
    @PostMapping("/admin/deleteQuestion")
    public ResponseEntity<?> deleteQuestion(@RequestParam("questionId") Long questionId) {
        try {
            questionRepository.deleteById(questionId);
            return ResponseEntity.ok("Question deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete the question: " + e.getMessage());
        }
    }

    
    @GetMapping("/admin/deletablePpts")
    @ResponseBody
    public List<Ppt> getDeletablePpts() {
        return fileService.findDeletablePpts();
    }

    @PostMapping("/admin/deletePpt/{pptId}")
    public String deletePpt(@PathVariable Long pptId, RedirectAttributes redirectAttributes) {
        try {
            fileService.deletePptAndRelatedData(pptId);
            redirectAttributes.addFlashAttribute("message", "PPT deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to delete PPT: " + e.getMessage());
        }
        return "redirect:/admin/ppt";
    }
}
