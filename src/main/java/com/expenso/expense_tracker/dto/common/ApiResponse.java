package com.expenso.expense_tracker.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API Response Wrapper
 *
 * Every REST API in Expensoo should return this object.
 *
 * Example:
 *
 * {
 *   "success": true,
 *   "message": "Transaction created successfully",
 *   "data": { ... }
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * Indicates whether the request was successful.
     */
    private boolean success;

    /**
     * Human-readable response message.
     */
    private String message;

    /**
     * Response payload.
     */
    private T data;

}