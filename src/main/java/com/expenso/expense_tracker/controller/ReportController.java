package com.expenso.expense_tracker.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.expenso.expense_tracker.dto.common.ApiResponse;
import com.expenso.expense_tracker.dto.report.ReportResponse;
import com.expenso.expense_tracker.security.JwtService;
import com.expenso.expense_tracker.service.ReportService;

import lombok.RequiredArgsConstructor;

/**
 * ============================================================
 * Report Controller
 * ============================================================
 *
 * Handles
 *
 * • Monthly Report
 * • Custom Date Range Report
 *
 * Base URL:
 *
 * /api/reports
 *
 * ============================================================
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

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
     * Monthly Report
     * ============================================================
     *
     * Example:
     *
     * GET /api/reports/monthly
     *
     * GET /api/reports/monthly?month=2026-07
     *
     * ============================================================
     */
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<ReportResponse>> getMonthlyReport(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @RequestParam(required = false)
            String month

    ) {

        UUID userId = extractUserId(

                authorizationHeader

        );

        YearMonth selectedMonth =

                (month == null || month.isBlank())

                        ? YearMonth.now()

                        : YearMonth.parse(month);

        ReportResponse response =

                reportService.getMonthlyReport(

                        userId,

                        selectedMonth

                );

        ApiResponse<ReportResponse> apiResponse =

                ApiResponse.<ReportResponse>builder()

                        .success(true)

                        .message("Monthly report generated successfully.")

                        .data(response)

                        .build();

        return ResponseEntity.ok(

                apiResponse

        );

    }

    /**
     * ============================================================
     * Custom Report
     * ============================================================
     *
     * Example:
     *
     * GET /api/reports/custom
     *      ?startDate=2026-01-01
     *      &endDate=2026-01-31
     *
     * ============================================================
     */
    @GetMapping("/custom")
    public ResponseEntity<ApiResponse<ReportResponse>> getCustomReport(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @RequestParam
            LocalDate startDate,

            @RequestParam
            LocalDate endDate

    ) {

        UUID userId = extractUserId(

                authorizationHeader

        );

        ReportResponse response =

                reportService.getReport(

                        userId,

                        startDate,

                        endDate,

                        "Custom Report"

                );

        ApiResponse<ReportResponse> apiResponse =

                ApiResponse.<ReportResponse>builder()

                        .success(true)

                        .message("Report generated successfully.")

                        .data(response)

                        .build();

        return ResponseEntity.ok(

                apiResponse

        );

    }

}