package com.expenso.expense_tracker.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.expenso.expense_tracker.dto.category.CategoryResponse;
import com.expenso.expense_tracker.dto.common.ApiResponse;
import com.expenso.expense_tracker.security.JwtService;
import com.expenso.expense_tracker.service.CategoryService;

import lombok.RequiredArgsConstructor;

/**
 * ============================================================
 * Category Controller
 * ============================================================
 *
 * Handles
 *
 * • Get User Categories
 *
 * Base URL:
 *
 * /api/categories
 *
 * ============================================================
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

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
     * Get User Categories
     * ============================================================
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories(

            @RequestHeader("Authorization")
            String authorizationHeader

    ) {

        UUID userId = extractUserId(

                authorizationHeader

        );

        List<CategoryResponse> response =

                categoryService.getCategories(

                        userId

                );

        ApiResponse<List<CategoryResponse>> apiResponse =

                ApiResponse.<List<CategoryResponse>>builder()

                        .success(true)

                        .message("Categories fetched successfully.")

                        .data(response)

                        .build();

        return ResponseEntity.ok(

                apiResponse

        );

    }

}