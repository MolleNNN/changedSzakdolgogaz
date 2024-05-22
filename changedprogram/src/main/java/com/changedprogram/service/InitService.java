package com.changedprogram.service;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.changedprogram.entity.Image;
import com.changedprogram.entity.Language;
import com.changedprogram.entity.Ppt;
import com.changedprogram.entity.Question;
import com.changedprogram.entity.Type;
import com.changedprogram.repository.CompanyRepository;
import com.changedprogram.repository.ImageRepository;
import com.changedprogram.repository.LanguageRepository;
import com.changedprogram.repository.PositionRepository;
import com.changedprogram.repository.PptRepository;
import com.changedprogram.repository.QuestionRepository;
import com.changedprogram.repository.ReceiverRepository;
import com.changedprogram.repository.TypeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class InitService {

    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ReceiverRepository receiverRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private ResourceLoader resourceLoader;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PptRepository pptRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private TypeRepository typeRepository;
    
    @Transactional
    public void processFile() throws IOException, InvalidFormatException {
        File file = new File("src/main/resources/static/default.pptx");
        boolean isNewPptCreated = false;

        // Check if any PPT is marked as active
        if (pptRepository.findByIsActiveTrue().isEmpty()) {
            Optional<Ppt> firstPpt = pptRepository.findFirstByOrderByIdAsc();
            if (firstPpt.isPresent()) {
                // If there are PPTs but none are active, set the first one to active
                Ppt ppt = firstPpt.get();
                ppt.setActive(true);
                pptRepository.save(ppt);
            } else {
                // No PPTs at all, create a new one and set it as active
                Ppt ppt = new Ppt();
                ppt.setFilename(file.getName());
                ppt.setActive(true); // Set this one as active since it's the only one
                // Set language to Hungarian by default
                Language language = languageRepository.findByCode("hu").orElseThrow(() -> new IllegalStateException("Default language not found"));
                ppt.setLanguage(language);

                // Set the type to 'EHS oktatás' by default
                Type defaultType = typeRepository.findByName("EHS oktatás").orElseThrow(() -> new IllegalStateException("Default type not found"));
                ppt.setType(defaultType);

                ppt = pptRepository.save(ppt);
                isNewPptCreated = true;

                // Convert PPT to images and save
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    XMLSlideShow pptx = new XMLSlideShow(inputStream);
                    Dimension pgsize = pptx.getPageSize();
                    for (XSLFSlide slide : pptx.getSlides()) {
                        BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
                        Graphics2D graphics = img.createGraphics();
                        // best rendering settings
                        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        slide.draw(graphics);
                        graphics.dispose();

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(img, "png", baos);
                        byte[] imageBytes = baos.toByteArray();

                        Image image = new Image();
                        image.setImage(imageBytes);
                        image.setPpt(ppt);
                        imageRepository.save(image);
                    }
                }
            }
        }

        // If a new PPT was created, upload the questions
        if (isNewPptCreated) {
            if (questionRepository.count() == 0) { // Check if the Question table is empty
                Path sqlFilePath = Paths.get("src/main/resources/questions.sql");
                if (Files.exists(sqlFilePath)) {
                    List<String> sqlStatements = Files.readAllLines(sqlFilePath);
                    Ppt existingPpt = pptRepository.findFirstByOrderByIdAsc()
                                      .orElseThrow(() -> new IllegalStateException("PPT not found"));
                    for (String sql : sqlStatements) {
                        parseSqlAndSaveQuestion(sql, existingPpt);
                    }
                }
            }
        }
    }
    
    private void parseSqlAndSaveQuestion(String sql, Ppt ppt) {
        try {
            // Remove the INSERT INTO part and extract the VALUES part only
            String valuesPart = sql.substring(sql.indexOf("VALUES (") + "VALUES (".length(), sql.lastIndexOf(")"));

            // Manually handle the parsing of values considering commas within text literals
            List<String> parts = new ArrayList<>();
            StringBuilder currentPart = new StringBuilder();
            boolean inQuotes = false;
            for (char ch : valuesPart.toCharArray()) {
                if (ch == '\'' && (currentPart.length() == 0 || currentPart.charAt(currentPart.length() - 1) != '\\')) {
                    inQuotes = !inQuotes;  // Toggle the inQuotes flag when encountering a quote
                } else if (ch == ',' && !inQuotes) {
                    parts.add(currentPart.toString().trim());  // Add the part if we encounter a comma outside of quotes
                    currentPart.setLength(0);  // Reset the builder for the next part
                    continue;
                }
                currentPart.append(ch);
            }
            parts.add(currentPart.toString().trim()); // Add the last part

            if (parts.size() == 3) { // Changed from 4 to 3
                String text = parts.get(0).replaceAll("^'(.*)'$", "$1");
                boolean answer = Boolean.parseBoolean(parts.get(1));
                Long pptId = Long.parseLong(parts.get(2));

                // Check PPT id consistency
                if (!ppt.getId().equals(pptId)) {
                    throw new IllegalStateException("PPT id in SQL does not match the provided PPT entity");
                }

                Question question = new Question();
                question.setText(text);
                question.setAnswer(answer);
                question.setPpt(ppt);
                questionRepository.save(question);
                System.out.println("Saved question: " + text);
            }
        } catch (Exception e) {
            System.err.println("Error processing SQL statement: " + sql);
            e.printStackTrace();
        }
    }
        
    
    @Transactional
    public void initPositions() {
        if (positionRepository.count() == 0) {
            executeSqlFromFile("classpath:positions.sql");
        }
    }

    @Transactional
    public void initCompanies() {
        if (companyRepository.count() == 0) {
            executeSqlFromFile("classpath:companies.sql");
        }
    }

    @Transactional
    public void initReceivers() {
        if (receiverRepository.count() == 0) {
            executeSqlFromFile("classpath:receivers.sql");
        }
    }

    @Transactional
    public void initLanguages() {
        if (languageRepository.count() == 0) {
            executeSqlFromFile("classpath:languages.sql");
        }
    }
    
    @Transactional
    public void initTypes() {
        if (typeRepository.count() == 0) {
            executeSqlFromFile("classpath:types.sql");
        }
    }
    

    @Transactional
    public void initQuizAttemptTable() throws IOException {
        Path executedFlag = Paths.get("quizattempt_initialized.flag");
        if (!Files.exists(executedFlag)) {
            executeSqlFromFile("classpath:quizattempt.sql");
            Files.createFile(executedFlag);
        }
    }

    @Transactional
    public void executeSqlFromFile(String filePath) {
        try {
            Resource resource = resourceLoader.getResource(filePath);
            InputStream inputStream = resource.getInputStream();
            String sqlFileContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String[] sqlStatements = sqlFileContent.split(";\\s*\\r?\\n");
            for (String statement : sqlStatements) {
                if (!statement.trim().isEmpty()) {
                    executeSql(statement.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void executeSql(String sql) {
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}