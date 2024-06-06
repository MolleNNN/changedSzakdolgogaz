package com.changedprogram.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.changedprogram.dto.ReceiverDTO;
import com.changedprogram.entity.Receiver;
import com.changedprogram.service.AdminReceiverService;

import jakarta.validation.ConstraintViolationException;

@Controller  // Marks this class as a Spring MVC controller
public class AdminReceiverController {

    @Autowired  // Injects the AdminReceiverService bean
    private AdminReceiverService adminReceiverService;

    @GetMapping("/admin/receivers")  // Handles GET requests for /admin/receivers
    public String getAllReceivers(Model model) {
        List<ReceiverDTO> receivers = adminReceiverService.findAllReceivers();  // Retrieves a list of all receivers
        model.addAttribute("receivers", receivers);  // Adds the list of receivers to the model
        model.addAttribute("newReceiver", new Receiver());  // Adds a new Receiver object to the model
        return "receivers";  // Returns the view name "receivers"
    }

    @PostMapping("/admin/receivers")  // Handles POST requests for /admin/receivers
    @ResponseBody  // Indicates the return value will be written directly to the HTTP response body
    public ResponseEntity<Map<String, Object>> addReceiver(@ModelAttribute("newReceiver") Receiver receiver, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        try {
            Receiver savedReceiver = adminReceiverService.addReceiver(receiver);  // Adds a new receiver
            response.put("success", true);
            response.put("message", "Fogadó fél sikeresen hozzáadva!");  // Success message
            response.put("receiver", savedReceiver);
        } catch (ConstraintViolationException e) {
            response.put("success", false);
            response.put("message", e.getMessage().replaceAll("\\[.*?\\]", ""));  // Handles validation errors
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());  // Handles illegal argument errors
        }

        return ResponseEntity.ok(response);  // Returns the response as an HTTP entity
    }

    @GetMapping("/admin/getAllReceivers")  // Handles GET requests for /admin/getAllReceivers
    @ResponseBody  // Indicates the return value will be written directly to the HTTP response body
    public List<ReceiverDTO> getAllReceivers() {
        return adminReceiverService.findAllReceivers();  // Returns a list of all receivers
    }

    @PostMapping("/admin/updateReceiver")  // Handles POST requests for /admin/updateReceiver
    @ResponseBody  // Indicates the return value will be written directly to the HTTP response body
    public ResponseEntity<?> updateReceiver(@RequestParam("receiverId") Long receiverId, @RequestParam("name") String newName) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminReceiverService.updateReceiver(receiverId, newName);  // Updates the receiver's name
            response.put("success", true);
            response.put("message", "Fogadó fél sikeresen módosítva!");  // Success message
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());  // Handles illegal argument errors
        }
        return ResponseEntity.ok(response);  // Returns the response as an HTTP entity
    }

    @GetMapping("/admin/getDeletableReceivers")  // Handles GET requests for /admin/getDeletableReceivers
    @ResponseBody  // Indicates the return value will be written directly to the HTTP response body
    public List<Receiver> getDeletableReceivers() {
        return adminReceiverService.getDeletableReceivers();  // Returns a list of deletable receivers
    }

    @PostMapping("/admin/deleteReceiver/{receiverId}")  // Handles POST requests for /admin/deleteReceiver/{receiverId}
    @ResponseBody  // Indicates the return value will be written directly to the HTTP response body
    public ResponseEntity<?> deleteReceiver(@PathVariable Long receiverId) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminReceiverService.deleteReceiver(receiverId);  // Deletes the receiver
            response.put("success", true);
            response.put("message", "Fogadó fél sikeresen törölve!");  // Success message
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());  // Handles illegal argument errors
        }
        return ResponseEntity.ok(response);  // Returns the response as an HTTP entity
    }
}
