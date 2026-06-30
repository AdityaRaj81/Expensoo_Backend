package com.expenso.expense_tracker.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Debug controller to verify environment variables on Render
 * REMOVE THIS IN PRODUCTION!
 */
@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @GetMapping("/env")
    public Map<String, String> checkEnvironment() {
        Map<String, String> result = new HashMap<>();
        
        String dbUrl = System.getenv("DATABASE_URL");
        
        if (dbUrl == null || dbUrl.isEmpty()) {
            result.put("status", "ERROR");
            result.put("message", "DATABASE_URL environment variable is NOT SET");
            result.put("solution", "Go to Render Dashboard -> Web Service -> Environment tab and add DATABASE_URL");
        } else {
            // Mask password for security
            String masked = dbUrl.replaceAll(":[^@]+@", ":****@");
            result.put("status", "SUCCESS");
            result.put("message", "DATABASE_URL is SET");
            result.put("masked_value", masked);
            result.put("format", dbUrl.startsWith("postgresql://") ? "Correct format" : "WARNING: Wrong format");
        }
        
        return result;
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "UP");
        result.put("message", "Backend is running");
        return result;
    }
}
