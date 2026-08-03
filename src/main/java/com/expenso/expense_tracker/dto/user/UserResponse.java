package com.expenso.expense_tracker.dto.user;

import com.expenso.expense_tracker.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User Response DTO
 *
 * Used whenever user information is returned to the frontend.
 *
 * Password is intentionally excluded.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    /**
     * User ID
     */
    private UUID id;

    /**
     * Full Name
     */
    private String name;

    /**
     * Email Address
     */
    private String email;

    /**
     * User Role
     */
    private UserRole role;

    /**
     * Account Status
     */
    private boolean active;

    /**
     * Account Created Time
     */
    private LocalDateTime createdAt;

}