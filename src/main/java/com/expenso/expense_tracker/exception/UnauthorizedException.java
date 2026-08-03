package com.expenso.expense_tracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when user authentication or authorization fails.
 *
 * Examples:
 * - Invalid JWT Token
 * - Expired Token
 * - Login Required
 * - Invalid Credentials
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Unauthorized access.");
    }

    public UnauthorizedException(String message) {
        super(message);
    }

}