package com.changedprogram.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.changedprogram.dto.PositionDTO;
import com.changedprogram.entity.Position;
import com.changedprogram.repository.PositionRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Service
public class AdminPositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private Validator validator;

    public List<PositionDTO> findAllPositions() {
        return positionRepository.findAll().stream()
                .sorted(Comparator.comparing(Position::getName))
                .map(position -> new PositionDTO(position.getId(), position.getName()))
                .collect(Collectors.toList());
    }


    public Position addPosition(Position position) throws ConstraintViolationException {
        validatePosition(position);
        String formattedName = formatPositionName(position.getName());
        position.setName(formattedName);

        Optional<Position> existingPosition = positionRepository.findByNameIgnoreCase(formattedName);
        if (existingPosition.isPresent()) {
            throw new IllegalArgumentException("A pozíció már létezik!");
        }

        return positionRepository.save(position);
    }

    public void updatePosition(Long positionId, String newName) {
        newName = newName.trim();
        validatePositionName(newName);
        Position existingPosition = positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("A pozíció nem található, frissítsd az oldalt! "));

        String formattedName = formatPositionName(newName);
        if (positionRepository.existsByName(formattedName)) {
            throw new IllegalArgumentException("A pozíció már létezik!");
        }

        existingPosition.setName(formattedName);
        positionRepository.save(existingPosition);
    }

    public List<Position> getDeletablePositions() {
        return positionRepository.findAll().stream()
                .filter(position -> position.getUsers().isEmpty())
                .sorted(Comparator.comparing(Position::getName))
                .collect(Collectors.toList());
    }


    public void deletePosition(Long positionId) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("A pozíció nem található, frissítsd az oldalt!"));

        if (!position.getUsers().isEmpty()) {
            throw new IllegalArgumentException("A pozíció nem törölhető mivel használatban van!");
        }

        positionRepository.delete(position);
    }

    private void validatePosition(Position position) throws ConstraintViolationException {
        // Trim the name before validation
        position.setName(position.getName().trim());
        
        Set<ConstraintViolation<Position>> violations = validator.validate(position);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("<br>"));
            throw new ConstraintViolationException(errorMessage, violations);
        }
    }

    private void validatePositionName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("A pozíció neve nem lehet üres!");
        }

        if (name.length() < 2 || name.length() > 255) {
            throw new IllegalArgumentException("A pozíció nevének 2 és 255 karakter között kell lennie");
        }

    }

    private String formatPositionName(String name) {
        return capitalizeFirstLetter(name.trim().replaceAll("\\s+", " "));
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}