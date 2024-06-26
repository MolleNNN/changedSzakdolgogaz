package com.changedprogram.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.changedprogram.dto.ReceiverDTO;
import com.changedprogram.entity.Receiver;
import com.changedprogram.repository.ReceiverRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Service
public class AdminReceiverService {

    @Autowired
    private ReceiverRepository receiverRepository;

    @Autowired
    private Validator validator;

    public List<ReceiverDTO> findAllReceivers() {
        return receiverRepository.findAll().stream()
                .map(receiver -> new ReceiverDTO(receiver.getId(), receiver.getName()))
                .collect(Collectors.toList());
    }

    public Receiver addReceiver(Receiver receiver) throws ConstraintViolationException {
        validateReceiver(receiver);
        String formattedName = formatReceiverName(receiver.getName());
        receiver.setName(formattedName);

        Optional<Receiver> existingReceiver = receiverRepository.findByNameIgnoreCase(formattedName);
        if (existingReceiver.isPresent()) {
            throw new IllegalArgumentException("A fogadó fél már létezik!");
        }

        return receiverRepository.save(receiver);
    }

    public void updateReceiver(Long receiverId, String newName) {
        newName = newName.trim();
        validateReceiverName(newName);
        Receiver existingReceiver = receiverRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("A fogadó fél nem található, frissítsd az oldalt!"));

        String formattedName = formatReceiverName(newName);
        if (receiverRepository.existsByName(formattedName)) {
            throw new IllegalArgumentException("A fogadó fél már létezik!");
        }

        existingReceiver.setName(formattedName);
        receiverRepository.save(existingReceiver);
    }

    public List<Receiver> getDeletableReceivers() {
        return receiverRepository.findAll().stream()
                .filter(receiver -> receiver.getUsers().isEmpty())
                .collect(Collectors.toList());
    }

    public void deleteReceiver(Long receiverId) {
        Receiver receiver = receiverRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("A fogadó fél nem található, frissítsd az oldalt!"));

        if (!receiver.getUsers().isEmpty()) {
            throw new IllegalArgumentException("A fogadó fél nem törölhető mivel használatban van!");
        }

        receiverRepository.delete(receiver);
    }

    private void validateReceiver(Receiver receiver) throws ConstraintViolationException {
        // Trim the name before validation
        receiver.setName(receiver.getName().trim());
        
        Set<ConstraintViolation<Receiver>> violations = validator.validate(receiver);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("<br>"));
            throw new ConstraintViolationException(errorMessage, violations);
        }
    }

    private void validateReceiverName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("A fogadó fél neve nem lehet üres!");
        }

        if (name.length() < 2 || name.length() > 255) {
            throw new IllegalArgumentException("A fogadó fél nevének 2 és 255 karakter között kell lennie");
        }

        Pattern pattern = Pattern.compile("^[A-Za-zÀ-ÿ-.\\s]+[A-Za-zÀ-ÿ-.\\s]*$");
        if (!pattern.matcher(name.trim()).matches()) {
            throw new IllegalArgumentException("A név csak betűket, kötőjeleket, pontokat és ékezetes karaktereket tartalmazhat.");
        }
    }

    private String formatReceiverName(String name) {
        return capitalizeFirstLetter(name.trim().replaceAll("\\s+", " "));
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}