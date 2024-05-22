package com.changedprogram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.changedprogram.service.FileService;
import com.changedprogram.service.InitService;

@Component
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private InitService initService;

    @Override
    public void run(String... args) throws Exception {
        initService.initTypes();     // Initialize types
        initService.initLanguages(); // Initialize languages
        initService.initPositions(); // Initialize positions
        initService.initCompanies(); // Initialize companies
        initService.initReceivers(); // Initialize receivers
        initService.processFile();   // Process additional files
        initService.initQuizAttemptTable(); // Initialize quiz_attempt table if not already done
    }
}