package com.expenso.expense_tracker.dto.auth;

import com.expenso.expense_tracker.dto.user.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login Response DTO
 *
 * Returned after successful authentication.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT Authentication Token
     */
    private String token;

    /**
     * Logged-in User Details
     */
    private UserResponse user;

}