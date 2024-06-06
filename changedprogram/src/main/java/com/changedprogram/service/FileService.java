package com.changedprogram.service;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import javax.imageio.ImageIO;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.changedprogram.entity.Company;
import com.changedprogram.entity.Image;
import com.changedprogram.entity.Language;
import com.changedprogram.entity.Position;
import com.changedprogram.entity.Ppt;
import com.changedprogram.entity.Question;
import com.changedprogram.entity.Receiver;
import com.changedprogram.entity.Type;
import com.changedprogram.entity.UploadResult;
import com.changedprogram.repository.CompanyRepository;
import com.changedprogram.repository.ImageRepository;
import com.changedprogram.repository.LanguageRepository;
import com.changedprogram.repository.PositionRepository;
import com.changedprogram.repository.PptRepository;
import com.changedprogram.repository.QuestionRepository;
import com.changedprogram.repository.ReceiverRepository;
import com.changedprogram.repository.ResultRepository;
import com.changedprogram.repository.TypeRepository;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;


@Service
public class FileService {

    @Autowired
    private PptRepository pptRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private PositionRepository positionRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ReceiverRepository receiverRepository;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private Validator validator;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private TypeRepository typeRepository;
    
    private static final int BATCH_SIZE = 50; // Adjust batch size based on your context
    
    
    @Transactional
    public Ppt storePpt(String name, MultipartFile file, String languageCode, Long typeId) throws IOException, InvalidFormatException {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }

        // Trim unnecessary white spaces
        name = name.trim();

        // Store the PPT metadata
        Ppt ppt = new Ppt();
        ppt.setFilename(name);
        ppt.setActive(false); // Set as inactive by default

        Language language = languageRepository.findByCode(languageCode)
                .orElseThrow(() -> new IllegalArgumentException("Helytelen nyelv kód: " + languageCode));
        ppt.setLanguage(language);

        Type type = typeRepository.findById(typeId)
                .orElseThrow(() -> new IllegalArgumentException("Helytelen típus ID: " + typeId));
        ppt.setType(type);

        ppt = pptRepository.save(ppt);

        // Convert the file to an array of bytes
        InputStream inputStream = file.getInputStream();
        XMLSlideShow pptx = new XMLSlideShow(inputStream);
        Dimension pgsize = pptx.getPageSize();
        List<XSLFSlide> slides = pptx.getSlides();

        // Convert each slide to an image
        for (XSLFSlide slide : slides) {
            BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();

            // Best rendering settings
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            slide.draw(graphics);
            graphics.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            // Store each image in the database
            Image image = new Image();
            image.setImage(imageBytes);
            image.setPpt(ppt);
            imageRepository.save(image);
        }
        return ppt;
    }



    
    @Transactional
    public void addQuestion(Long pptId, String questionText, boolean answer) throws ConstraintViolationException {
        Ppt ppt = pptRepository.findById(pptId)
                .orElseThrow(() -> new IllegalArgumentException("Helytelen ppt ID: " + pptId));

        questionText = questionText.trim().replaceAll("\\s+", " ");

        Question question = new Question();
        question.setPpt(ppt);
        question.setText(questionText);
        question.setAnswer(answer);

        Set<ConstraintViolation<Question>> violations = validator.validate(question);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Question> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new ConstraintViolationException("Validálási hibák: " + sb.toString(), violations);
        }

        questionRepository.save(question);
    }


    
    @Transactional
    public String uploadQuestionsFromExcel(MultipartFile file, Long pptId) throws Exception {
        if (!file.getOriginalFilename().endsWith(".xlsx") && !file.getOriginalFilename().endsWith(".xls")) {
            throw new IllegalArgumentException("Érvénytelen fájlformátum. Kérjük Excel fájlt töltsön fel.");
        }

        Ppt ppt = pptRepository.findById(pptId)
                .orElseThrow(() -> new IllegalArgumentException("Helytelen ppt ID: " + pptId));

        int insertedCount = 0;
        int duplicateCount = 0;
        int missingDataCount = 0;

        try (InputStream inputStream = file.getInputStream(); Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header row
            if (rows.hasNext()) {
                rows.next();
            }

            Set<String> existingQuestions = questionRepository.findByPptId(pptId)
                    .stream()
                    .map(Question::getText)
                    .collect(Collectors.toSet());

            Set<String> newQuestions = new HashSet<>();

            while (rows.hasNext()) {
                Row row = rows.next();

                // Skip rows with missing question or answer
                if (row.getCell(0) == null || row.getCell(1) == null) {
                    missingDataCount++;
                    continue;
                }

                // Get and format question text
                Cell questionCell = row.getCell(0);
                String questionText;
                if (questionCell.getCellType() == CellType.STRING) {
                    questionText = capitalizeFirstLetter(questionCell.getStringCellValue().trim().replaceAll("\\s+", " "));
                } else {
                    missingDataCount++;
                    continue; // Skip row if question is not a string
                }

                // Normalize and validate answer text
                Cell answerCell = row.getCell(1);
                String answerText;
                if (answerCell.getCellType() == CellType.STRING) {
                    answerText = answerCell.getStringCellValue().trim().toLowerCase();
                } else if (answerCell.getCellType() == CellType.BOOLEAN) {
                    answerText = answerCell.getBooleanCellValue() ? "true" : "false";
                } else {
                    missingDataCount++;
                    continue; // Skip row if answer is not a string or boolean
                }

                // Convert answer text to boolean
                boolean answer;
                switch (answerText) {
                    case "true":
                        answer = true;
                        break;
                    case "false":
                        answer = false;
                        break;
                    default:
                        missingDataCount++;
                        continue; // Skip row if answer is invalid
                }

                // Check for duplicate questions per PPT
                if (existingQuestions.contains(questionText) || newQuestions.contains(questionText)) {
                    duplicateCount++;
                    continue; // Skip duplicate questions
                }

                newQuestions.add(questionText);

                Question question = new Question();
                question.setPpt(ppt);
                question.setText(questionText);
                question.setAnswer(answer);

                Set<ConstraintViolation<Question>> violations = validator.validate(question);
                if (!violations.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (ConstraintViolation<Question> violation : violations) {
                        sb.append(violation.getMessage()).append("\n");
                    }
                    throw new ConstraintViolationException("Validálási hibák: " + sb.toString(), violations);
                }

                questionRepository.save(question);
                insertedCount++;
            }
        }

        return String.format("A kérdések feltöltése sikeres volt! %d új kérdés hozzáadva, %d duplikált kérdés kihagyva, %d sor kihagyva hiányzó adatok miatt.", insertedCount, duplicateCount, missingDataCount);
    }


    
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
    
    
    
    
    @Transactional
    public void deletePptAndRelatedData(Long pptId) {
        Ppt ppt = pptRepository.findById(pptId)
                .orElseThrow(() -> new IllegalArgumentException("PPT nem található az ID-val: " + pptId));

        if (ppt.isActive()) {
            throw new IllegalStateException("Ppt nem törölhető. Kérlek frissítsd az oldalt!");
        }
        List<Image> images = imageRepository.findByPptId(pptId);
        imageRepository.deleteAll(images);

        List<Question> questions = questionRepository.findByPptId(pptId);
        questionRepository.deleteAll(questions);

        pptRepository.deleteById(pptId);
    }

    
    public List<Ppt> findDeletablePpts() {
        List<Ppt> allPpts = pptRepository.findAll();
        return allPpts.stream()
                .filter(ppt -> !ppt.isActive() && !resultRepository.existsByPptId(ppt.getId()))
                .collect(Collectors.toList());
    }
    
    
    @Transactional
    public void importPositionsFromExcel(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if (currentRow.getRowNum() != 0) { // Skip header
                    String positionName = currentRow.getCell(0).getStringCellValue();
                    Position position = new Position();
                    position.setName(positionName);
                    positionRepository.save(position);
                }
            }
        }
    }
    
    
    @Transactional
    public UploadResult importDataFromExcel(MultipartFile file) throws IOException {
        UploadResult result = new UploadResult();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            while (rows.hasNext()) {
                Row row = rows.next();
                if (row.getRowNum() == 0) continue; // Skip header

                String positionName = safelyGetCellValue(row.getCell(0));
                String companyName = safelyGetCellValue(row.getCell(1));
                String receiverName = safelyGetCellValue(row.getCell(2));

                // Process Position
                if (!positionName.isEmpty()) {
                    Position position = new Position();
                    position.setName(positionName);
                    validateAndAddPosition(position, result);
                }

                // Process Company
                if (!companyName.isEmpty()) {
                    Company company = new Company();
                    company.setName(companyName);
                    validateAndAddCompany(company, result);
                }

                // Process Receiver
                if (!receiverName.isEmpty()) {
                    Receiver receiver = new Receiver();
                    receiver.setName(receiverName);
                    validateAndAddReceiver(receiver, result);
                }
            }
        }
        return result;
    }

    
    private void validateAndAddPosition(Position position, UploadResult result) {
        Set<ConstraintViolation<Position>> violations = validator.validate(position);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<Position> violation : violations) {
                String errorMessage = String.format("'%s' %s", position.getName(), violation.getMessage());
                result.addFailure("Positions", errorMessage);
            }
        } else if (!positionRepository.existsByName(position.getName())) {
            positionRepository.save(position);
            result.addSuccess("Positions", "Added position: " + position.getName());
        } else {
            String errorMessage = String.format("'%s' Position already exists", position.getName());
            result.addFailure("Positions", errorMessage);
        }
    }

    
    private void validateAndAddCompany(Company company, UploadResult result) {
        Set<ConstraintViolation<Company>> violations = validator.validate(company);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<Company> violation : violations) {
                String errorMessage = String.format("'%s' %s", company.getName(), violation.getMessage());
                result.addFailure("Company", errorMessage);
            }
        } else if (!companyRepository.existsByName(company.getName())) {
            companyRepository.save(company);
            result.addSuccess("Company", "Added company: " + company.getName());
        } else {
            String errorMessage = String.format("'%s' Company already exists", company.getName());
            result.addFailure("Company", errorMessage);
        }
    }

    
    private void validateAndAddReceiver(Receiver receiver, UploadResult result) {
        Set<ConstraintViolation<Receiver>> violations = validator.validate(receiver);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<Receiver> violation : violations) {
                String errorMessage = String.format("'%s' %s", receiver.getName(), violation.getMessage());
                result.addFailure("Receiver", errorMessage);
            }
        } else if (!receiverRepository.existsByName(receiver.getName())) {
            receiverRepository.save(receiver);
            result.addSuccess("Receiver", "Added receiver: " + receiver.getName());
        } else {
            String errorMessage = String.format("'%s' Receiver already exists", receiver.getName());
            result.addFailure("Receiver", errorMessage);
        }
    }



    
    private String safelyGetCellValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return "";
        } else if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return cell.toString().trim();
    } 
}
    


