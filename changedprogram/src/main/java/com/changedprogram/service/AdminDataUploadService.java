package com.changedprogram.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.changedprogram.entity.Company;
import com.changedprogram.entity.Position;
import com.changedprogram.entity.Receiver;
import com.changedprogram.entity.UploadResult;
import com.changedprogram.repository.CompanyRepository;
import com.changedprogram.repository.PositionRepository;
import com.changedprogram.repository.ReceiverRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class AdminDataUploadService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private ReceiverRepository receiverRepository;

    @Autowired
    private Validator validator;

    @Transactional
    public String processExcelFile(MultipartFile file) throws Exception {
        StringBuilder feedback = new StringBuilder();
        StringBuilder positionFeedback = new StringBuilder();
        StringBuilder companyFeedback = new StringBuilder();
        StringBuilder receiverFeedback = new StringBuilder();

        int successfulPositions = 0, unsuccessfulPositions = 0;
        int successfulCompanies = 0, unsuccessfulCompanies = 0;
        int successfulReceivers = 0, unsuccessfulReceivers = 0;

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isHeaderRow = true;
            for (Row row : sheet) {
                if (isHeaderRow) {
                    isHeaderRow = false;
                    continue; // Skip header row
                }

                // Skip empty rows
                if (row == null || (row.getCell(0) == null && row.getCell(1) == null && row.getCell(2) == null)) {
                    continue;
                }

                String position = cleanUpSpaces(getCellValue(row.getCell(0)).trim());
                String company = cleanUpSpaces(getCellValue(row.getCell(1)).trim());
                String receiver = cleanUpSpaces(getCellValue(row.getCell(2)).trim());

                // Process position
                if (position.startsWith("Helytelen mezőtípus")) {
                    positionFeedback.append(position).append("\n");
                    unsuccessfulPositions++;
                } else if (!position.isEmpty() && validateDataLength(position)) {
                    try {
                        Position positionEntity = new Position(position);
                        validateEntity(positionEntity);
                        if (!positionRepository.existsByName(position)) {
                            positionRepository.save(positionEntity);
                            successfulPositions++;
                            positionFeedback.append("'").append(position).append("' sikeresen hozzáadva.\n");
                        } else {
                            unsuccessfulPositions++;
                            positionFeedback.append("'").append(position).append("' már létezik.\n");
                        }
                    } catch (ConstraintViolationException e) {
                        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                            String message = violation.getMessage();
                            positionFeedback.append(position).append("': ").append(message).append("\n");
                        }
                    }
                } else if (!position.isEmpty()) {
                    unsuccessfulPositions++;
                    positionFeedback.append("'").append(position).append("' 2 és 255 karakter között kell lennie.\n");
                }

                // Process company
                if (company.startsWith("Helytelen mezőtípus")) {
                    companyFeedback.append(company).append("\n");
                    unsuccessfulCompanies++;
                } else if (!company.isEmpty() && validateDataLength(company)) {
                    try {
                        Company companyEntity = new Company(company);
                        validateEntity(companyEntity);
                        if (!companyRepository.existsByName(company)) {
                            companyRepository.save(companyEntity);
                            successfulCompanies++;
                            companyFeedback.append("'").append(company).append("' sikeresen hozzáadva.\n");
                        } else {
                            unsuccessfulCompanies++;
                            companyFeedback.append("'").append(company).append("' már létezik.\n");
                        }
                    } catch (ConstraintViolationException e) {
                        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                            String message = violation.getMessage();
                            companyFeedback.append(company).append("': ").append(message).append("\n");
                        }
                    }
                } else if (!company.isEmpty()) {
                    unsuccessfulCompanies++;
                    companyFeedback.append("'").append(company).append("' 2 és 255 karakter között kell lennie.\n");
                }

                // Process receiver
                if (receiver.startsWith("Helytelen mezőtípus")) {
                    receiverFeedback.append(receiver).append("\n");
                    unsuccessfulReceivers++;
                } else if (!receiver.isEmpty() && validateDataLength(receiver)) {
                    try {
                        Receiver receiverEntity = new Receiver(receiver);
                        validateEntity(receiverEntity);
                        if (!receiverRepository.existsByName(receiver)) {
                            receiverRepository.save(receiverEntity);
                            successfulReceivers++;
                            receiverFeedback.append("'").append(receiver).append("' sikeresen hozzáadva.\n");
                            
             
                            
                        } else {
                            unsuccessfulReceivers++;
                            receiverFeedback.append("'").append(receiver).append("' már létezik.\n");
                        }
                    } catch (ConstraintViolationException e) {
                        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                            String message = violation.getMessage();
                            receiverFeedback.append("'").append(receiver).append("': ").append(message).append("\n");
                        }
                    }
                } else if (!receiver.isEmpty()) {
                    unsuccessfulReceivers++;
                    receiverFeedback.append("'").append(receiver).append("' 2 és 255 karakter között kell lennie.\\n");
                }
            }
        }

        feedback.append("Pozíciók:\n").append(positionFeedback).append("\nCégek:\n").append(companyFeedback).append("\nFogadó felek:\n").append(receiverFeedback);
        feedback.append("\nPozíciók - Sikeres: ").append(successfulPositions).append(", Sikertelen: ").append(unsuccessfulPositions);
        feedback.append("\nCégek - Sikeres: ").append(successfulCompanies).append(", Sikertelen: ").append(unsuccessfulCompanies);
        feedback.append("\nFogadó felek - Sikeres: ").append(successfulReceivers).append(", Sikertelen: ").append(unsuccessfulReceivers);

        return feedback.toString();
    }

    private void validateEntity(Object entity) {
        Set<ConstraintViolation<Object>> violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return "Helytelen mezőtípus: " + cell.getNumericCellValue();
            case BOOLEAN:
                return "Helytelen mezőtípus: " + cell.getBooleanCellValue();
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        return cell.getRichStringCellValue().getString().trim();
                    case NUMERIC:
                        return "Helytelen mezőtípus: " + cell.getNumericCellValue();
                    case BOOLEAN:
                        return "Helytelen mezőtípus: " + cell.getBooleanCellValue();
                    default:
                        return "Nem megfelelő cella formátum érzékelve";
                }
            default:
                return "Nem megfelelő cella formátum érzékelve";
        }
    }

    private boolean validateDataLength(String data) {
        return data != null && data.length() >= 2 && data.length() <= 255;
    }

    private String cleanUpSpaces(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        // Replace multiple spaces with a single space and trim the result
        return data.replaceAll("\\s{2,}", " ").trim();
    }
}