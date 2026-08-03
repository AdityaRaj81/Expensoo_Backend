package com.expenso.expense_tracker.repository;

import com.expenso.expense_tracker.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email (case-insensitive)
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if email already exists
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Find only active user
     */
    Optional<User> findByIdAndActiveTrue(UUID id);

    /**
     * Find active user by email.
     */
    Optional<User> findByEmailIgnoreCaseAndActiveTrue(String email);

    /**
     * Check active email.
     */
    boolean existsByEmailIgnoreCaseAndActiveTrue(String email);

}