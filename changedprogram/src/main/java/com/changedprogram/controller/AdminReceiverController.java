package com.changedprogram.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.changedprogram.dto.ReceiverDTO;
import com.changedprogram.entity.Receiver;
import com.changedprogram.repository.ReceiverRepository;
import com.changedprogram.service.AdminReceiverService;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@Controller
public class AdminReceiverController {

    @Autowired
    private AdminReceiverService adminReceiverService;

    @GetMapping("/admin/receivers")
    public String getAllReceivers(Model model) {
        List<ReceiverDTO> receivers = adminReceiverService.findAllReceivers();
        model.addAttribute("receivers", receivers);
        model.addAttribute("newReceiver", new Receiver());
        return "receivers";
    }

    @PostMapping("/admin/receivers")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addReceiver(@ModelAttribute("newReceiver") Receiver receiver, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        try {
            Receiver savedReceiver = adminReceiverService.addReceiver(receiver);
            response.put("success", true);
            response.put("message", "Receiver added successfully!");
            response.put("receiver", savedReceiver);
        } catch (ConstraintViolationException e) {
            response.put("success", false);
            response.put("message", e.getMessage().replaceAll("\\[.*?\\]", ""));
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/getAllReceivers")
    @ResponseBody
    public List<ReceiverDTO> getAllReceivers() {
        return adminReceiverService.findAllReceivers();
    }

    @PostMapping("/admin/updateReceiver")
    @ResponseBody
    public ResponseEntity<?> updateReceiver(@RequestParam("receiverId") Long receiverId, @RequestParam("name") String newName) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminReceiverService.updateReceiver(receiverId, newName);
            response.put("success", true);
            response.put("message", "Receiver updated successfully.");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/getDeletableReceivers")
    @ResponseBody
    public List<Receiver> getDeletableReceivers() {
        return adminReceiverService.getDeletableReceivers();
    }

    @PostMapping("/admin/deleteReceiver/{receiverId}")
    @ResponseBody
    public ResponseEntity<?> deleteReceiver(@PathVariable Long receiverId) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminReceiverService.deleteReceiver(receiverId);
            response.put("success", true);
            response.put("message", "Receiver deleted successfully!");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
