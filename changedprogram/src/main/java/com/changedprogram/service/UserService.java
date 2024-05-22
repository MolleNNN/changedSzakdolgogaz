package com.changedprogram.service;


import java.util.Base64;

import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;


import com.changedprogram.entity.Ppt;
import com.changedprogram.entity.User;
import com.changedprogram.entity.UserFormModel;
import com.changedprogram.repository.CompanyRepository;
import com.changedprogram.repository.ImageRepository;
import com.changedprogram.repository.PositionRepository;
import com.changedprogram.repository.PptRepository;
import com.changedprogram.repository.ReceiverRepository;
import com.changedprogram.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private ReceiverRepository receiverRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PptRepository pptRepository;
    
    @Autowired
    private ImageRepository imageRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Transactional
    public Long registerOrUpdateUser(UserFormModel userForm) {
        Optional<User> optionalUser = userRepository.findByTaxNumber(userForm.getTaxNumber());
        User user = optionalUser.orElse(new User());

        user.setName(userForm.getName());
        user.setBirthdate(userForm.getBirthdate());
        user.setEmail(userForm.getEmail());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setTaxNumber(userForm.getTaxNumber());
        user.setPosition(positionRepository.findById(userForm.getPositionId())
                                           .orElseThrow(() -> new IllegalArgumentException("Invalid Position ID")));
        user.setReceiver(receiverRepository.findById(userForm.getReceiverId())
                                           .orElseThrow(() -> new IllegalArgumentException("Invalid Receiver ID")));
        user.setCompany(companyRepository.findById(userForm.getCompanyId())
                                         .orElseThrow(() -> new IllegalArgumentException("Invalid Company ID")));
        user.setActive(true);

        user = userRepository.save(user);
        return user.getId();
    }

    public void prepareFormModel(Model model) {
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("receivers", receiverRepository.findAll());
        model.addAttribute("companies", companyRepository.findAll());
    }

    
    public void validateUserForm(UserFormModel userForm, BindingResult result) {
        // Custom validation for the birthdate
        try {
            userForm.validateBirthdate();
        } catch (Exception e) {
            result.rejectValue("birthdate", "error.adduser.birthdate", e.getMessage());
        }

        // Custom validation for the tax number
        if (!result.hasFieldErrors("taxNumber")) {
            try {
                if (!userForm.isValidTaxNumber()) {
                    throw new IllegalArgumentException(userForm.getMessage("error.adduser.taxNumber"));
                }
            } catch (IllegalArgumentException e) {
                result.rejectValue("taxNumber", "error.adduser.taxNumber", e.getMessage());
            }
        }
    }
    
    
    public List<Ppt> getActivePresentationsByLanguage(String languageCode) {
        return pptRepository.findActivePresentationsByLanguageCode(languageCode);
    }
  
    public List<String> getImagesByPptId(Long pptId) {
        return imageRepository.findByPptId(pptId)
                              .stream()
                              .map(image -> Base64.getEncoder().encodeToString(image.getImage()))
                              .collect(Collectors.toList());
    }

    public String preparePresentation(Long pptId, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String language = (String) session.getAttribute("sessionLanguage");

        // Log details before preparing the presentation
        logger.info("Preparing presentation for user ID: {}, pptId: {}, Language: {}", 
                    userId != null ? userId : "none", 
                    pptId, 
                    language != null ? language : "none");

        List<String> images = getImagesByPptId(pptId);
        if (images.isEmpty()) {
            model.addAttribute("message", "No images available for this presentation.");
        } else {
            model.addAttribute("images", images);
        }

        Ppt presentation = pptRepository.findById(pptId).orElse(null);
        if (presentation != null) {
            model.addAttribute("ppt", presentation);
        }

        return "presentation";
    }

}
 

