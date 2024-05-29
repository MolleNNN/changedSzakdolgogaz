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

import com.changedprogram.dto.PptDTO;
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
import com.changedprogram.service.AdminPptService;
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
    @Autowired
    private AdminPptService adminPptService;
	
/*	
    @GetMapping("/admin/ppt")
    public String pptPage(Model model) {
        List<Ppt> activePpts = pptRepository.findByIsActiveTrue();
        List<Ppt> allPpts = pptRepository.findAll();
        model.addAttribute("activePpts", activePpts);
        model.addAttribute("allPpts", allPpts);
        return "ppt";
    }
  */
    @GetMapping("/admin/ppt")
    public String pptPage(Model model) {
        List<PptDTO> allPpts = adminPptService.getAllPpts();
        List<PptDTO> pptWithQuestions = adminPptService.getPptsWithQuestions();
        List<Type> allTypes = adminPptService.getAllTypes();
        String activePptIds = allPpts.stream()
                                    .filter(PptDTO::isActive)
                                    .map(ppt -> String.valueOf(ppt.getId()))
                                    .collect(Collectors.joining(","));
        model.addAttribute("allPpts", allPpts);
        model.addAttribute("pptWithQuestions", pptWithQuestions);
        model.addAttribute("allTypes", allTypes);
        model.addAttribute("activePptIds", activePptIds);

        // Add these lines to pass languages and types
        model.addAttribute("languages", languageRepository.findAll());
        model.addAttribute("types", typeRepository.findAll());

        return "ppt";
    }


    @PostMapping("/changeActivePpt")
    public String changeActivePpt(@RequestParam List<Long> pptIds, RedirectAttributes redirectAttributes) {
        try {
            adminPptService.setActivePpts(pptIds);
            redirectAttributes.addFlashAttribute("successMessage", "PPTs activated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to activate PPTs: " + e.getMessage());
        }
        return "redirect:/admin/ppt";
    }

    
    @PostMapping("/uploadPpt")
    public String uploadPpt(@RequestParam("name") String name,
                            @RequestParam("file") MultipartFile file,
                            @RequestParam("languageCode") String languageCode,
                            @RequestParam("typeId") Long typeId,
                            RedirectAttributes redirectAttributes) {
        // Trim unnecessary white spaces
        name = name.trim();

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/admin/ppt";
        }

        // Validate file type
        if (!isPptFile(file)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid file type. Please upload a PPT file (.ppt or .pptx).");
            return "redirect:/admin/ppt";
        }

        // Validate PPT name uniqueness
        if (pptRepository.existsByFilename(name)) {
            redirectAttributes.addFlashAttribute("errorMessage", "A PPT with this name already exists. Please choose a different name.");
            return "redirect:/admin/ppt";
        }

        try {
            fileService.storePpt(name, file, languageCode, typeId);
            redirectAttributes.addFlashAttribute("successMessage", "PPT uploaded successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload PPT: " + e.getMessage());
            e.printStackTrace(); // Log the exception stack trace for debugging
        }
        return "redirect:/admin/ppt";
    }

    private boolean isPptFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        return contentType != null && (contentType.equals("application/vnd.ms-powerpoint") ||
               contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) &&
               fileName != null && (fileName.endsWith(".ppt") || fileName.endsWith(".pptx"));
    }


    @PostMapping("/admin/addType")
    public String addType(@RequestParam String name, @RequestParam Integer valid, RedirectAttributes redirectAttributes) {
        // Normalize the name by trimming and replacing multiple spaces with a single space
        name = name.trim().replaceAll("\\s+", " ");
        
        // Validate that the name has at least 3 non-space characters
        if (name.replaceAll("\\s+", "").length() < 3) {
            redirectAttributes.addFlashAttribute("errorMessage", "Type name must have at least 3 non-space characters.");
            return "redirect:/admin/ppt";
        }

        // Check if the name already exists in the Type repository
        if (typeRepository.existsByName(name)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Type name already exists!");
            return "redirect:/admin/ppt";
        }

        // Create and save the new type
        Type type = new Type();
        type.setName(name);
        type.setValid(valid);
        typeRepository.save(type);

        redirectAttributes.addFlashAttribute("successMessage", "Type added successfully!");
        return "redirect:/admin/ppt";
    }

    
    @PostMapping("/admin/updateType")
    public String updateType(@RequestParam Long typeId,
                             @RequestParam String name,
                             @RequestParam Integer valid,
                             RedirectAttributes redirectAttributes) {
        name = name.trim();

        if (name.replaceAll("\\s+", "").length() < 3) {
            redirectAttributes.addFlashAttribute("errorMessage", "Type name must have at least 3 non-space characters.");
            return "redirect:/admin/ppt";
        }

        // Check if the name exists for any type other than the current one
        if (typeRepository.existsByNameAndIdNot(name, typeId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Type name already exists!");
            return "redirect:/admin/ppt";
        }

        try {
            Type type = typeRepository.findById(typeId)
                                       .orElseThrow(() -> new IllegalArgumentException("Invalid type ID: " + typeId));
            type.setName(name);
            type.setValid(valid);
            typeRepository.save(type);
            redirectAttributes.addFlashAttribute("successMessage", "Type updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update type: " + e.getMessage());
        }
        return "redirect:/admin/ppt";
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
        List<Question> questions = questionRepository.findByPptIdOrderByTextAsc(pptId);
        List<Question> deletableQuestions = adminPptService.getDeletableQuestionsByPptId(pptId);
        model.addAttribute("pptId", pptId);
        model.addAttribute("pptName", ppt.getFilename());
        model.addAttribute("questions", questions);
        model.addAttribute("deletableQuestions", deletableQuestions);
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
        
        // Normalize the question text
        String normalizedQuestion = question.trim().replaceAll("\\s+", " ");
        
        // Check if the question is unique for the given PPT ID
        if (!adminPptService.isQuestionUnique(pptId, normalizedQuestion)) {
            redirectAttributes.addFlashAttribute("error", "A question with the same text already exists.");
            return "redirect:/admin/addquestions";
        }

        try {
            // Add the question using the normalized text
            fileService.addQuestion(pptId, normalizedQuestion, answer);
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
    
 /*   @PostMapping("/uploadPpt")
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
*/

    
    @PostMapping("/admin/modifyQuestion")
    @Transactional
    public String modifyQuestion(
            @RequestParam("questionId") Long questionId,
            @RequestParam("question") String questionText,
            @RequestParam("answer") boolean answer,
            RedirectAttributes redirectAttributes) {
        try {
            // Find the existing question
            Question existingQuestion = questionRepository.findById(questionId)
                    .orElseThrow(() -> new IllegalStateException("Question not found with ID: " + questionId));
            
            // Normalize the question text
            String normalizedQuestion = questionText.trim().replaceAll("\\s+", " ");

            // Check if the question is unique for the given PPT ID, excluding the current question
            if (!adminPptService.isQuestionUniqueForModification(existingQuestion.getPpt().getId(), normalizedQuestion, questionId)) {
                redirectAttributes.addFlashAttribute("error", "A question with the same text already exists.");
                return "redirect:/admin/addquestions";
            }

            // Update the question
            existingQuestion.setText(normalizedQuestion);
            existingQuestion.setAnswer(answer);

            // Save the updated question
            questionRepository.save(existingQuestion);

            // Set success message
            redirectAttributes.addFlashAttribute("message", "Question updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            // Generic exception handling
            redirectAttributes.addFlashAttribute("error", "Failed to update the question: " + e.getMessage());
        }
        return "redirect:/admin/addquestions"; // Redirect to the same page to display the message
    }



    
    @PostMapping("/admin/deleteQuestion")
    public String deleteQuestion(@RequestParam("questionId") Long questionId, RedirectAttributes redirectAttributes) {
        try {
            questionRepository.deleteById(questionId);
            redirectAttributes.addFlashAttribute("message", "Question deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete the question: " + e.getMessage());
        }
        return "redirect:/admin/addquestions"; // Redirect to the same page to display the message
    }



    
    @GetMapping("/admin/deletablePpts")
    @ResponseBody
    public List<Ppt> getDeletablePpts() {
        return fileService.findDeletablePpts();
    }
    
    @PostMapping("/admin/deletePpt")
    public String deletePpt(@RequestParam Long pptId, RedirectAttributes redirectAttributes) {
        try {
            fileService.deletePptAndRelatedData(pptId);
            redirectAttributes.addFlashAttribute("successMessage", "PPT deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete PPT: " + e.getMessage());
        }
        return "redirect:/admin/ppt";
    }

    
/*
    @PostMapping("/admin/deletePpt/{pptId}")
    public String deletePpt(@PathVariable Long pptId, RedirectAttributes redirectAttributes) {
        try {
            fileService.deletePptAndRelatedData(pptId);
            redirectAttributes.addFlashAttribute("message", "PPT deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to delete PPT: " + e.getMessage());
        }
        return "redirect:/admin/ppt";
    }*/
    
    
    
    
    
    
    
    
    @PostMapping("/admin/uploadQuestions")
    public String uploadQuestions(@RequestParam("file") MultipartFile file,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Long pptId = (Long) session.getAttribute("pptId");
        if (pptId == null) {
            redirectAttributes.addFlashAttribute("error", "No PPT selected.");
            return "redirect:/admin/ppt";
        }
        try {
            String resultMessage = fileService.uploadQuestionsFromExcel(file, pptId);
            redirectAttributes.addFlashAttribute("message", resultMessage);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload questions: " + e.getMessage());
        }
        return "redirect:/admin/addquestions";
    }
    
    
    
    
    @GetMapping("/admin/getPptDetails")
    @ResponseBody
    public ResponseEntity<PptDTO> getPptDetails(@RequestParam Long pptId) {
        PptDTO ppt = adminPptService.getPptById(pptId);
        return ResponseEntity.ok(ppt);
    }



    @PostMapping("/admin/updatePpt")
    public String updatePpt(@RequestParam Long pptId,
                            @RequestParam String name,
                            @RequestParam String languageCode,
                            @RequestParam Long typeId,
                            RedirectAttributes redirectAttributes) {
        name = name.trim();

        if (pptRepository.existsByFilenameAndIdNot(name, pptId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "A PPT with this name already exists. Please choose a different name.");
            return "redirect:/admin/ppt";
        }

        try {
            adminPptService.updatePpt(pptId, name, languageCode, typeId);
            redirectAttributes.addFlashAttribute("successMessage", "PPT updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update PPT: " + e.getMessage());
        }
        return "redirect:/admin/ppt";
    }
    
    
    
    
    
    
    
    
    
    
}
