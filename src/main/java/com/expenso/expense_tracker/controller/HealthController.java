package com.expenso.expense_tracker.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("status", "UP");
        response.put("service", "Expensoo Backend");
        response.put("version", "1.0.0");
        response.put("timestamp", Instant.now());

        return ResponseEntity.ok(response);
    }
}