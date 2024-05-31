package com.changedprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.changedprogram.entity.UploadResult;
import com.changedprogram.service.AdminDataUploadService;
import com.changedprogram.service.FileService;

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
        try {
            String result = adminDataUploadService.processExcelFile(file);
            redirectAttributes.addFlashAttribute("message", result);
            return "redirect:/admin/dataupload";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to upload file: " + e.getMessage());
            return "redirect:/admin/dataupload";
        }
    }
}