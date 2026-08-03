package com.expenso.expense_tracker.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expenso.expense_tracker.dto.auth.LoginRequest;
import com.expenso.expense_tracker.dto.auth.LoginResponse;
import com.expenso.expense_tracker.dto.auth.SignupRequest;
import com.expenso.expense_tracker.dto.user.UserResponse;
import com.expenso.expense_tracker.enums.UserRole;
import com.expenso.expense_tracker.exception.DuplicateResourceException;
import com.expenso.expense_tracker.exception.UnauthorizedException;
import com.expenso.expense_tracker.mapper.UserMapper;
import com.expenso.expense_tracker.model.User;
import com.expenso.expense_tracker.repository.UserRepository;
import com.expenso.expense_tracker.security.JwtService;

import lombok.RequiredArgsConstructor;

/**
 * ============================================================
 * Auth Service
 * ============================================================
 *
 * Handles:
 *
 * • User Registration
 * • User Login
 * • Password Encryption
 * • JWT Generation
 * • Authentication Validation
 *
 * ============================================================
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    /**
     * ============================================================
     * Register New User
     * ============================================================
     */
    public LoginResponse signup(SignupRequest request) {

        String email = normalizeEmail(
                request.getEmail()
        );

        /*
         * Check Duplicate Email
         */
        if (userRepository.existsByEmailIgnoreCase(email)) {

            throw new DuplicateResourceException(
                    "Email is already registered."
            );

        }

        /*
         * Create User Entity
         */
        User user = User.builder()

                .name(
                        request.getName().strip()
                )

                .email(email)

                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )

                .role(UserRole.USER)

                .active(true)

                .build();

        /*
         * Save User
         */
        User savedUser = userRepository.save(user);

        /*
         * Generate JWT Token
         */
        String token = jwtService.generateToken(

                savedUser.getId(),

                savedUser.getEmail(),

                savedUser.getRole()

        );

        /*
         * Convert Entity -> DTO
         */
        UserResponse userResponse = userMapper.toUserResponse(
                savedUser
        );

        /*
         * Return Login Response
         */
        return LoginResponse.builder()

                .token(token)

                .user(userResponse)

                .build();

    }

    /**
     * ============================================================
     * User Login
     * ============================================================
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        String email = normalizeEmail(
                request.getEmail()
        );

        /*
         * Find User
         */
        User user = userRepository

                .findByEmailIgnoreCase(email)

                .orElseThrow(() ->

                        new UnauthorizedException(
                                "Invalid email or password."
                        )

                );

        /*
         * Check Account Status
         */
        validateUser(user);

        /*
         * Verify Password
         */
        if (!passwordEncoder.matches(

                request.getPassword(),

                user.getPassword()

        )) {

            throw new UnauthorizedException(
                    "Invalid email or password."
            );

        }

        /*
         * Generate JWT
         */
        String token = jwtService.generateToken(

                user.getId(),

                user.getEmail(),

                user.getRole()

        );

        /*
         * Convert Entity -> DTO
         */
        UserResponse userResponse = userMapper.toUserResponse(
                user
        );

        /*
         * Return Response
         */
        return LoginResponse.builder()

                .token(token)

                .user(userResponse)

                .build();

    }

    /**
     * ============================================================
     * Validate User
     * ============================================================
     */
    private void validateUser(User user) {

        if (!user.isActive()) {

            throw new UnauthorizedException(
                    "Your account has been disabled. Please contact support."
            );

        }

    }

    /**
     * ============================================================
     * Normalize Email
     * ============================================================
     */
    private String normalizeEmail(String email) {

        return email

                .trim()

                .toLowerCase(Locale.ROOT);

    }

}