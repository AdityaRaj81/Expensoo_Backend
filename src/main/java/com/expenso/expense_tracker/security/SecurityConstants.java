package com.expenso.expense_tracker.security;

/**
 * Security Constants
 * Centralized constants used by the security layer.
 */
public final class SecurityConstants {

    private SecurityConstants() {

        // Prevent instantiation

    }

    /**
     * Authorization Header Name
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * JWT Token Prefix
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Login Endpoint
     */
    public static final String LOGIN_URL = "/api/auth/login";

    /**
     * Signup Endpoint
     */
    public static final String SIGNUP_URL = "/api/auth/signup";

    /**
     * Health Check Endpoint
     */
    public static final String HEALTH_URL = "/api/health";

    /**
     * Home Endpoint
     */
    public static final String HOME_URL = "/";

}