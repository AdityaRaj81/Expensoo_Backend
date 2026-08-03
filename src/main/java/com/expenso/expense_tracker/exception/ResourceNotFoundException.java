package com.expenso.expense_tracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested resource is not found.
 *
 * Example:
 * - User not found
 * - Transaction not found
 * - Category not found
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super("Requested resource was not found.");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

}