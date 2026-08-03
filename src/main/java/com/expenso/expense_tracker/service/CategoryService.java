package com.expenso.expense_tracker.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expenso.expense_tracker.dto.category.CategoryResponse;
import com.expenso.expense_tracker.model.Transaction;
import com.expenso.expense_tracker.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

/**
 * ============================================================
 * Category Service
 * ============================================================
 *
 * Handles
 *
 * • User Categories
 * • Unique Categories
 * • Sorted Categories
 *
 * ============================================================
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final TransactionRepository transactionRepository;

    /**
     * ============================================================
     * Get User Categories
     * ============================================================
     */
    public List<CategoryResponse> getCategories(UUID userId) {

        List<Transaction> transactions = transactionRepository

                .findByUserIdOrderByDateDesc(userId);

        return transactions.stream()

                .map(Transaction::getCategory)

                .filter(category -> category != null && !category.isBlank())

                .map(this::normalizeCategory)

                .distinct()

                .sorted(String.CASE_INSENSITIVE_ORDER)

                .map(category ->

                        CategoryResponse.builder()

                                .name(category)

                                .build()

                )

                .toList();

    }

    /**
     * ============================================================
     * Check Category Exists
     * ============================================================
     */
    public boolean categoryExists(

            UUID userId,

            String category

    ) {

        return getCategories(userId)

                .stream()

                .anyMatch(response ->

                        response.getName()

                                .equalsIgnoreCase(

                                        normalizeCategory(category)

                                )

                );

    }

    /**
     * ============================================================
     * Normalize Category
     * ============================================================
     */
    private String normalizeCategory(String category) {

        return category

                .trim()

                .replaceAll("\\s+", " ");

    }

}