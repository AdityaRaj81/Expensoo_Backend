package com.expenso.expense_tracker.controller;

import java.time.YearMonth;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.expenso.expense_tracker.dto.common.ApiResponse;
import com.expenso.expense_tracker.dto.dashboard.DashboardResponse;
import com.expenso.expense_tracker.security.JwtService;
import com.expenso.expense_tracker.service.DashboardService;

import lombok.RequiredArgsConstructor;

/**
 * ============================================================
 * Dashboard Controller
 * ============================================================
 *
 * Handles
 *
 * • Dashboard Summary
 * • Monthly Overview
 * • Expense Breakdown
 * • Recent Transactions
 *
 * Base URL:
 *
 * /api/dashboard
 *
 * ============================================================
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    private final JwtService jwtService;

    /**
     * ============================================================
     * Extract User ID From JWT
     * ============================================================
     *
     * Temporary implementation.
     *
     * Later this will be replaced by
     * @AuthenticationPrincipal.
     *
     * ============================================================
     */
    private UUID extractUserId(String authorizationHeader) {

        return jwtService.extractUserId(

                authorizationHeader

        );

    }

    /**
     * ============================================================
     * Get Dashboard
     * ============================================================
     *
     * Example:
     *
     * GET /api/dashboard
     *
     * GET /api/dashboard?month=2026-07
     *
     * ============================================================
     */
    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @RequestParam(required = false)
            String month

    ) {

        UUID userId = extractUserId(

                authorizationHeader

        );

        DashboardResponse response;

        if (month == null || month.isBlank()) {

            response = dashboardService.getDashboard(

                    userId

            );

        } else {

            response = dashboardService.getDashboard(

                    userId,

                    YearMonth.parse(month)

            );

        }

        ApiResponse<DashboardResponse> apiResponse =

                ApiResponse.<DashboardResponse>builder()

                        .success(true)

                        .message("Dashboard fetched successfully.")

                        .data(response)

                        .build();

        return ResponseEntity.ok(

                apiResponse

        );

    }

}