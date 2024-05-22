package com.changedprogram.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


import com.changedprogram.dto.PositionDTO;
import com.changedprogram.entity.Position;
import com.changedprogram.service.AdminPositionService;

import jakarta.validation.ConstraintViolationException;


@Controller
public class AdminPositionController {

    @Autowired
    private AdminPositionService adminPositionService;

    @GetMapping("/admin/positions")
    public String getAllPositions(Model model) {
        List<PositionDTO> positions = adminPositionService.findAllPositions();
        model.addAttribute("positions", positions);
        model.addAttribute("newPosition", new Position());
        return "positions";
    }

    @PostMapping("/admin/positions")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addPosition(@ModelAttribute("newPosition") Position position, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        try {
            Position savedPosition = adminPositionService.addPosition(position);
            response.put("success", true);
            response.put("message", "Position added successfully!");
            response.put("position", savedPosition);
        } catch (ConstraintViolationException e) {
            response.put("success", false);
            response.put("message", e.getMessage().replaceAll("\\[.*?\\]", ""));
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/getAllPositions")
    @ResponseBody
    public List<PositionDTO> getAllPositions() {
        return adminPositionService.findAllPositions();
    }

    @PostMapping("/admin/updatePosition")
    @ResponseBody
    public ResponseEntity<?> updatePosition(@RequestParam("positionId") Long positionId, @RequestParam("name") String newName) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminPositionService.updatePosition(positionId, newName);
            response.put("success", true);
            response.put("message", "Position updated successfully.");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/getDeletablePositions")
    @ResponseBody
    public List<Position> getDeletablePositions() {
        return adminPositionService.getDeletablePositions();
    }

    @PostMapping("/admin/deletePosition/{positionId}")
    @ResponseBody
    public ResponseEntity<?> deletePosition(@PathVariable Long positionId) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminPositionService.deletePosition(positionId);
            response.put("success", true);
            response.put("message", "Position deleted successfully!");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}