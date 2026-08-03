package com.expenso.expense_tracker.controller;

import com.expenso.expense_tracker.dto.auth.LoginRequest;
import com.expenso.expense_tracker.dto.auth.LoginResponse;
import com.expenso.expense_tracker.dto.auth.SignupRequest;
import com.expenso.expense_tracker.dto.common.ApiResponse;
import com.expenso.expense_tracker.service.AuthService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ============================================================
 * Authentication Controller
 * ============================================================
 *
 * Handles
 *
 * • User Registration
 * • User Login
 *
 * Base URL:
 *
 * /api/auth
 *
 * ============================================================
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    /**
     * ============================================================
     * Register User
     * ============================================================
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<LoginResponse>> signup(

            @Valid
            @RequestBody
            SignupRequest request

    ) {

        LoginResponse response = authService.signup(request);

        ApiResponse<LoginResponse> apiResponse =

                ApiResponse.<LoginResponse>builder()

                        .success(true)

                        .message("Account created successfully.")

                        .data(response)

                        .build();

        return ResponseEntity

                .status(HttpStatus.CREATED)

                .body(apiResponse);

    }

    /**
     * ============================================================
     * Login User
     * ============================================================
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(

            @Valid
            @RequestBody
            LoginRequest request

    ) {

        LoginResponse response = authService.login(request);

        ApiResponse<LoginResponse> apiResponse =

                ApiResponse.<LoginResponse>builder()

                        .success(true)

                        .message("Login successful.")

                        .data(response)

                        .build();

        return ResponseEntity.ok(

                apiResponse

        );

    }

}