package com.expenso.expense_tracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when request data is invalid.
 *
 * Examples:
 * - Invalid Amount
 * - Invalid Category
 * - Missing Required Field
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        super("Invalid request.");
    }

    public BadRequestException(String message) {
        super(message);
    }

}