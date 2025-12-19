package com.expenso.expense_tracker.controller;

import com.expenso.expense_tracker.dto.DashboardResponse;
import com.expenso.expense_tracker.security.JwtService;
import com.expenso.expense_tracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getDashboardData(
            @RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("DEBUG: Dashboard request with authHeader present: " + (authHeader != null));
            if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
                System.err.println("DEBUG: Invalid auth header format");
                return ResponseEntity.status(401).body("Missing or invalid Authorization header");
            }
            UUID userId = jwtService.extractUserId(authHeader);
            System.out.println("DEBUG: Fetching dashboard for userId: " + userId);
            DashboardResponse response = dashboardService.getDashboardData(userId);
            System.out.println("DEBUG: Dashboard response received with " + (response != null ? "data" : "null"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("DEBUG: Error fetching dashboard: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch dashboard data: " + e.getMessage());
        }
    }
}
