package com.expenso.expense_tracker.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.expenso.expense_tracker.dto.common.ApiResponse;
import com.expenso.expense_tracker.dto.user.UserResponse;
import com.expenso.expense_tracker.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * ============================================================
 * User Controller
 * ============================================================
 *
 * Handles
 *
 * • Get User Profile
 * • Get Current User
 *
 * Base URL:
 *
 * /api/users
 *
 * ============================================================
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * ============================================================
     * Get User By ID
     * ============================================================
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(

            @PathVariable
            UUID id

    ) {

        UserResponse response =

                userService.getUserById(id);

        ApiResponse<UserResponse> apiResponse =

                ApiResponse.<UserResponse>builder()

                        .success(true)

                        .message("User fetched successfully.")

                        .data(response)

                        .build();

        return ResponseEntity.ok(

                apiResponse

        );

    }

    /**
     * ============================================================
     * Get Current Logged-in User
     * ============================================================
     *
     * This endpoint is useful after login.
     *
     * It will later use:
     *
     * @AuthenticationPrincipal UserPrincipal
     *
     * ============================================================
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(

            @RequestHeader("Authorization")
            String token

    ) {

        UserResponse response =

                userService.getCurrentUser(token);

        ApiResponse<UserResponse> apiResponse =

                ApiResponse.<UserResponse>builder()

                        .success(true)

                        .message("Current user fetched successfully.")

                        .data(response)

                        .build();

        return ResponseEntity.ok(

                apiResponse

        );

    }

}