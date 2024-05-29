package com.changedprogram.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.changedprogram.dto.PptDTO;
import com.changedprogram.entity.Ppt;
import com.changedprogram.entity.Question;
import com.changedprogram.entity.Type;
import com.changedprogram.repository.LanguageRepository;
import com.changedprogram.repository.PptRepository;
import com.changedprogram.repository.QuestionRepository;
import com.changedprogram.repository.TypeRepository;

import jakarta.transaction.Transactional;

@Service
public class AdminPptService {
    
    @Autowired
    private PptRepository pptRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private LanguageRepository languageRepository;

    public List<PptDTO> getAllPpts() {
        List<Ppt> ppts = pptRepository.findAll();
        // Sort the list by filename in ascending order
        ppts.sort(Comparator.comparing(Ppt::getFilename));

        List<PptDTO> pptDTOs = new ArrayList<>();

        for (Ppt ppt : ppts) {
            PptDTO dto = new PptDTO();
            dto.setId(ppt.getId());
            dto.setFilename(ppt.getFilename());
            dto.setLanguageName(ppt.getLanguage().getName());
            dto.setTypeName(ppt.getType().getName());
            dto.setActive(ppt.isActive());
            pptDTOs.add(dto);
        }

        return pptDTOs;
    }
    public List<Type> getAllTypes() {
        return typeRepository.findAllByOrderByNameAsc();
    }
    public List<PptDTO> getPptsWithQuestions() {
        List<Ppt> ppts = pptRepository.findPptsWithQuestions();
        return ppts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public void setActivePpts(List<Long> pptIds) {
        List<Ppt> allPpts = pptRepository.findAll();
        allPpts.forEach(ppt -> ppt.setActive(false));

        List<Ppt> activePpts = pptRepository.findAllById(pptIds);
        activePpts.forEach(ppt -> ppt.setActive(true));

        pptRepository.saveAll(allPpts);
    }

    private PptDTO convertToDto(Ppt ppt) {
        PptDTO dto = new PptDTO();
        dto.setId(ppt.getId());
        dto.setFilename(ppt.getFilename());
        dto.setLanguageName(ppt.getLanguage().getName());
        dto.setTypeName(ppt.getType().getName());
        dto.setActive(ppt.isActive());
        return dto;
    }
    
    public List<Question> getDeletableQuestionsByPptId(Long pptId) {
        return questionRepository.findDeletableQuestionsByPptId(pptId);
    }
    
    
    //question valid

    public boolean isQuestionUnique(Long pptId, String questionText) {
        // Normalize the question text
        String normalizedQuestion = questionText.trim().replaceAll("\\s+", " ").toLowerCase();

        // Check if a question with the same text (ignoring case and whitespace) exists for the given PPT ID
        return questionRepository.findByPptIdAndNormalizedText(pptId, normalizedQuestion).isEmpty();
    }
    private String normalizeText(String text) {
        return text.trim().replaceAll("\\s+", " ").toLowerCase();
    }
    
    public PptDTO getPptById(Long pptId) {
        Ppt ppt = pptRepository.findById(pptId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid PPT ID: " + pptId));
        
        PptDTO dto = new PptDTO();
        dto.setId(ppt.getId());
        dto.setFilename(ppt.getFilename());
        dto.setLanguageName(ppt.getLanguage().getName());
        dto.setTypeName(ppt.getType().getName());
        dto.setActive(ppt.isActive());  // Set the active field
        return dto;
    }



    @Transactional
    public void updatePpt(Long pptId, String name, String languageCode, Long typeId) {
        Ppt ppt = pptRepository.findById(pptId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid PPT ID: " + pptId));

        ppt.setFilename(name);
        ppt.setLanguage(languageRepository.findByCode(languageCode)
            .orElseThrow(() -> new IllegalArgumentException("Invalid language code: " + languageCode)));
        ppt.setType(typeRepository.findById(typeId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid type ID: " + typeId)));

        pptRepository.save(ppt);
    }

    public boolean isTypeNameUnique(String name) {
        return !typeRepository.existsByName(name.trim());
    }
    
    public boolean isQuestionUniqueForModification(Long pptId, String questionText, Long questionId) {
        return !questionRepository.existsByPptIdAndTextAndNotId(pptId, questionText, questionId);
    }
}