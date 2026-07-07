package com.expenso.expense_tracker.controller;

import com.expenso.expense_tracker.dto.DashboardResponse;
import com.expenso.expense_tracker.security.JwtService;
import com.expenso.expense_tracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final DashboardService dashboardService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getReports(@RequestHeader("Authorization") String authHeader) {
        try {
            UUID userId = jwtService.extractUserId(authHeader);
            DashboardResponse response = dashboardService.getDashboardData(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to fetch reports");
        }
    }
}
