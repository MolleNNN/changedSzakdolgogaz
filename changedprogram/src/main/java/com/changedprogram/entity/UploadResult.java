package com.changedprogram.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadResult {

    private Map<String, Integer> successCounts = new HashMap<>();
    private Map<String, Integer> failureCounts = new HashMap<>();
    private List<String> successMessages = new ArrayList<>();
    private List<String> failureMessages = new ArrayList<>();

    public void addSuccess(String category, String message) {
        successCounts.put(category, successCounts.getOrDefault(category, 0) + 1);
        successMessages.add(message);
    }

    public void addFailure(String category, String message) {
        failureCounts.put(category, failureCounts.getOrDefault(category, 0) + 1);
        failureMessages.add(message);
    }

    public void incrementFailureCount(String category) {
        failureCounts.put(category, failureCounts.getOrDefault(category, 0) + 1);
    }

    // Getters and setters
    public Map<String, Integer> getSuccessCounts() {
        return successCounts;
    }

    public Map<String, Integer> getFailureCounts() {
        return failureCounts;
    }

    public List<String> getSuccessMessages() {
        return successMessages;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }
}