package com.expenso.expense_tracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when trying to create a resource
 * that already exists.
 *
 * Example:
 * - Email already exists
 * - Category already exists
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException() {
        super("Resource already exists.");
    }

    public DuplicateResourceException(String message) {
        super(message);
    }

}