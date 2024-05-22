package com.changedprogram.service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.changedprogram.EncryptionUtil;
import com.changedprogram.dto.ResultDTO;
import com.changedprogram.dto.UserrDTO;
import com.changedprogram.entity.User;
import com.changedprogram.repository.UserRepository;

@Service
public class AdminDataService {

    @Autowired
    private UserRepository userRepository;
    
 /*   
    public List<UserrDTO> getAllUsers() {
        List<User> users = userRepository.findAllWithDetails1();
        return users.stream().map(user -> {
            List<ResultDTO> results = user.getResults().stream().map(result -> 
                new ResultDTO(result.getId(), result.getFilled(), result.isNotificationSent(), result.getValid(), result.getPpt().getFilename())
            ).collect(Collectors.toList());
            return new UserrDTO(user.getId(), user.getName(), user.getEmail(), user.isActive(), user.getBirthdate(), 
                user.getPosition().getName(), user.getReceiver().getName(), user.getCompany().getName(), 
                user.getTaxNumber(), user.getPhoneNumber(), results);
        }).collect(Collectors.toList());
    }*/
  
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
                                            decryptedSignature
                                    );
                                })
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
    
    
    
/*
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
                                .map(result -> new ResultDTO(
                                        result.getId(),
                                        result.getFilled(),
                                        result.isNotificationSent(),
                                        result.getValid(),
                                        result.getPpt() != null ? result.getPpt().getFilename() : null))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}*/