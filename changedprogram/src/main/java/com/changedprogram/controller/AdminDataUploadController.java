package com.changedprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.changedprogram.entity.UploadResult;
import com.changedprogram.service.AdminDataUploadService;
import com.changedprogram.service.FileService;

@Controller
public class AdminDataUploadController {

    @Autowired
    private FileService fileService;
	
    @GetMapping("/admin/dataupload")
    public String showUploadPage(Model model, @ModelAttribute("result") UploadResult result) {
        System.out.println("After redirect, Success Counts: " + result.getSuccessCounts()); // Debugging
        System.out.println("After redirect, Failure Counts: " + result.getFailureCounts()); // Debugging
        model.addAttribute("result", result);
        return "dataupload";
    }


    @PostMapping("/admin/uploadData")
    public String uploadData(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to upload.");
            return "redirect:/admin/dataupload";
        }

        // Validate file type
        if (!isExcelFile(file)) {
            redirectAttributes.addFlashAttribute("error", "Invalid file type. Please upload an Excel file (.xls or .xlsx).");
            return "redirect:/admin/dataupload";
        }

        try {
            UploadResult result = fileService.importDataFromExcel(file);
            redirectAttributes.addFlashAttribute("result", result);
            redirectAttributes.addFlashAttribute("successMessages", result.getSuccessMessages());
            redirectAttributes.addFlashAttribute("failureMessages", result.getFailureMessages());
            redirectAttributes.addFlashAttribute("message", "Data processed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload data: " + e.getMessage());
            e.printStackTrace(); // Log the exception stack trace for debugging
        }
        return "redirect:/admin/dataupload";
    }

    private boolean isExcelFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        return contentType != null && (contentType.equals("application/vnd.ms-excel") ||
               contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) &&
               fileName != null && (fileName.endsWith(".xls") || fileName.endsWith(".xlsx"));
    }

    
    
    
    
}

