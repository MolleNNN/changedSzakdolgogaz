package com.changedprogram.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.changedprogram.entity.Company;
import com.changedprogram.entity.Position;
import com.changedprogram.entity.Receiver;
import com.changedprogram.entity.UploadResult;
import com.changedprogram.repository.CompanyRepository;
import com.changedprogram.repository.PositionRepository;
import com.changedprogram.repository.ReceiverRepository;

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

    public String processExcelFile(MultipartFile file) throws Exception {
        List<String> feedback = new ArrayList<>();
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

                String position = getCellValue(row.getCell(0));
                String company = getCellValue(row.getCell(1));
                String receiver = getCellValue(row.getCell(2));

                // Process position
                if (validateData(position)) {
                    if (!positionRepository.existsByName(position)) {
                        positionRepository.save(new Position(position));
                        successfulPositions++;
                        feedback.add("Position '" + position + "' was successfully added.");
                    } else {
                        unsuccessfulPositions++;
                        feedback.add("Position '" + position + "' already exists in the database.");
                    }
                } else {
                    unsuccessfulPositions++;
                    feedback.add("Position '" + position + "' is invalid (must be at least 3 characters).");
                }

                // Process company
                if (validateData(company)) {
                    if (!companyRepository.existsByName(company)) {
                        companyRepository.save(new Company(company));
                        successfulCompanies++;
                        feedback.add("Company '" + company + "' was successfully added.");
                    } else {
                        unsuccessfulCompanies++;
                        feedback.add("Company '" + company + "' already exists in the database.");
                    }
                } else {
                    unsuccessfulCompanies++;
                    feedback.add("Company '" + company + "' is invalid (must be at least 3 characters).");
                }

                // Process receiver
                if (validateData(receiver)) {
                    if (!receiverRepository.existsByName(receiver)) {
                        receiverRepository.save(new Receiver(receiver));
                        successfulReceivers++;
                        feedback.add("Receiver '" + receiver + "' was successfully added.");
                    } else {
                        unsuccessfulReceivers++;
                        feedback.add("Receiver '" + receiver + "' already exists in the database.");
                    }
                } else {
                    unsuccessfulReceivers++;
                    feedback.add("Receiver '" + receiver + "' is invalid (must be at least 3 characters).");
                }
            }
        }

        feedback.add("Positions - Successful: " + successfulPositions + ", Unsuccessful: " + unsuccessfulPositions);
        feedback.add("Companies - Successful: " + successfulCompanies + ", Unsuccessful: " + unsuccessfulCompanies);
        feedback.add("Receivers - Successful: " + successfulReceivers + ", Unsuccessful: " + unsuccessfulReceivers);

        return String.join("\n", feedback);
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return cell.getStringCellValue().trim();
    }

    private boolean validateData(String data) {
        return data != null && data.replace(" ", "").length() >= 3;
    }
}