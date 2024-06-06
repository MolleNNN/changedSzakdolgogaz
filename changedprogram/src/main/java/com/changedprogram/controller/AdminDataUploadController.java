package com.changedprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.changedprogram.service.AdminDataUploadService;

@Controller
public class AdminDataUploadController {

    @Autowired
    private AdminDataUploadService adminDataUploadService;

    @GetMapping("/admin/dataupload")
    public String showUploadPage(Model model) {
        return "dataupload";
    }

    @PostMapping("/admin/uploadData")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String fileName = file.getOriginalFilename();
        if (fileName != null && (fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))) {
            try {
                String result = adminDataUploadService.processExcelFile(file);
                redirectAttributes.addFlashAttribute("message", result);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Nem sikerült a fájl feltöltése: " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Érvénytelen fájltípus. Kérjük, egy Excel fájlt töltön fel (.xls vagy .xlsx).");
        }
        return "redirect:/admin/dataupload";
    }
}