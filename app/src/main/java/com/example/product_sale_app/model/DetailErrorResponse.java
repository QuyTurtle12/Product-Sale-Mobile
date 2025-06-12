package com.example.product_sale_app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class DetailErrorResponse {
    @SerializedName("type")
    private String type;

    @SerializedName("title")
    private String title;

    @SerializedName("status")
    private int status;

    // The 'errors' field is a Map where keys are field names (e.g., "Password")
    // and values are lists of error messages for that field.
    @SerializedName("errors")
    private Map<String, List<String>> errors;

    @SerializedName("traceId")
    private String traceId;

    // Default constructor
    public DetailErrorResponse() {
    }

    // Getters
    public String getType() { return type; }
    public String getTitle() { return title; }
    public int getStatus() { return status; }
    public Map<String, List<String>> getErrors() { return errors; }
    public String getTraceId() { return traceId; }

    //Helper method to get the first error message for a specific field.
    public String getFirstErrorForField(String fieldName) {
        if (errors != null && errors.containsKey(fieldName)) {
            List<String> fieldErrors = errors.get(fieldName);
            if (fieldErrors != null && !fieldErrors.isEmpty()) {
                return fieldErrors.get(0);
            }
        }
        return null;
    }

    // Helper method to get all error messages as a single formatted string.
    public String getAllErrorMessages() {
        if (errors == null || errors.isEmpty()) {
            return title != null ? title : "An error occurred."; // Fallback to title
        }
        StringBuilder messages = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : errors.entrySet()) {
            // messages.append(entry.getKey()).append(": "); // Optional: include field name
            for (String message : entry.getValue()) {
                messages.append(message).append("\n");
            }
        }
        // Remove the last newline character
        if (messages.length() > 0) {
            messages.setLength(messages.length() - 1);
        }
        return messages.toString();
    }
}
