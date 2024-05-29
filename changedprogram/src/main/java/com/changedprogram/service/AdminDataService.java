package com.changedprogram.service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.changedprogram.EncryptionUtil;
import com.changedprogram.dto.ResultDTO;
import com.changedprogram.dto.UserrDTO;
import com.changedprogram.entity.Company;
import com.changedprogram.entity.Position;
import com.changedprogram.entity.Receiver;
import com.changedprogram.entity.User;
import com.changedprogram.repository.CompanyRepository;
import com.changedprogram.repository.PositionRepository;
import com.changedprogram.repository.ReceiverRepository;
import com.changedprogram.repository.UserRepository;

@Service
public class AdminDataService {

    @Autowired
    private UserRepository userRepository;
      
    @Autowired
    private EncryptionUtil encryptionUtil;

    public List<UserrDTO> getAllUsers() {
        List<User> users = userRepository.findAllWithDetails1();
        return users.stream()
                .map(user -> new UserrDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.isActive(),
                        user.getBirthdate(),
                        user.getPosition() != null ? user.getPosition().getName() : null,
                        user.getReceiver() != null ? user.getReceiver().getName() : null,
                        user.getCompany() != null ? user.getCompany().getName() : null,
                        user.getTaxNumber(),
                        user.getPhoneNumber(),
                        user.getResults().stream()
                                .map(result -> {
                                    String decryptedSignature = "";
                                    if (result.getSignature() != null) {
                                        try {
                                            decryptedSignature = encryptionUtil.decrypt(result.getSignature());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    return new ResultDTO(
                                            result.getId(),
                                            result.getFilled(),
                                            result.isNotificationSent(),
                                            result.getValid(),
                                            result.getPpt() != null ? result.getPpt().getFilename() : null,
                                            decryptedSignature,
                                            result.getPpt() != null && result.getPpt().getLanguage() != null ? result.getPpt().getLanguage().getName() : null,
                                            result.getPpt() != null && result.getPpt().getType() != null ? result.getPpt().getType().getName() : null
                                    );
                                })
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
    
    
    //Modify data
    
    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ReceiverRepository receiverRepository;
    
    public Iterable<User> getAllUsers1() {
        return userRepository.findAll();
    }

    public Iterable<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    public Iterable<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Iterable<Receiver> getAllReceivers() {
        return receiverRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User ID: " + userId));
    }

    public boolean isDuplicateTaxNumber(User userFormData) {
        Optional<User> existingUserWithSameTaxNumber = userRepository.findByTaxNumber(userFormData.getTaxNumber());
        return existingUserWithSameTaxNumber.isPresent() && 
                !existingUserWithSameTaxNumber.get().getId().equals(userFormData.getId());
    }

    public void updateUser(User userFormData) {
        User existingUser = userRepository.findById(userFormData.getId())
                                          .orElseThrow(() -> new RuntimeException("User not found"));

        // Format name to capitalize the first letter of each word
        String formattedName = capitalizeWords(userFormData.getName());
        existingUser.setName(formattedName);

        // Convert email to lowercase
        String formattedEmail = userFormData.getEmail().toLowerCase();
        existingUser.setEmail(formattedEmail);

        existingUser.setPhoneNumber(userFormData.getPhoneNumber());
        existingUser.setBirthdate(userFormData.getBirthdate());
        existingUser.setTaxNumber(userFormData.getTaxNumber());
        existingUser.setActive(userFormData.isActive());

        Position position = positionRepository.findById(userFormData.getPosition().getId()).orElse(null);
        existingUser.setPosition(position);

        Company company = companyRepository.findById(userFormData.getCompany().getId()).orElse(null);
        existingUser.setCompany(company);

        Receiver receiver = receiverRepository.findById(userFormData.getReceiver().getId()).orElse(null);
        existingUser.setReceiver(receiver);

        userRepository.save(existingUser);
    }

    private String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        String[] words = str.split("\\s+");
        StringBuilder capitalizedWords = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                capitalizedWords.append(Character.toUpperCase(word.charAt(0)))
                                .append(word.substring(1).toLowerCase());
                capitalizedWords.append(" ");
            }
        }

        // Trim the trailing space
        return capitalizedWords.toString().trim();
    }
}
