package com.changedprogram.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.changedprogram.entity.Company;
import com.changedprogram.entity.Position;
import com.changedprogram.entity.Receiver;
import com.changedprogram.repository.CompanyRepository;
import com.changedprogram.repository.PositionRepository;
import com.changedprogram.repository.ReceiverRepository;

import org.apache.poi.ss.usermodel.Sheet;
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

    
    public void processFile(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<String> messages = new ArrayList<>();
            boolean isHeader = true;

            for (Row row : sheet) {
                // Skip header row
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String companyName = row.getCell(0).getStringCellValue().trim();
                String positionName = row.getCell(1).getStringCellValue().trim();
                String receiverName = row.getCell(2).getStringCellValue().trim();

                // Validate and process each cell
                if (isValidName(companyName)) {
                    Company company = new Company();
                    company.setName(formatName(companyName));
                    companyRepository.save(company);
                }
                if (isValidName(positionName)) {
                    Position position = new Position();
                    position.setName(formatName(positionName));
                    positionRepository.save(position);
                }
                if (isValidName(receiverName)) {
                    Receiver receiver = new Receiver();
                    receiver.setName(formatName(receiverName));
                    receiverRepository.save(receiver);
                }
            }
        }
    }

    private boolean isValidName(String name) {
        return name != null && name.length() >= 2 && name.matches("^[A-Za-zÀ-ÿ-.\\s]+$");
    }

    private String formatName(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}