package com.expenso.expense_tracker.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.expenso.expense_tracker.enums.TransactionType;
import com.expenso.expense_tracker.model.Transaction;

@Repository
public interface TransactionRepository
                extends JpaRepository<Transaction, Long>,
                JpaSpecificationExecutor<Transaction> {

        List<Transaction> findByUserIdOrderByDateDesc(
                        UUID userId);

        Page<Transaction> findByUserId(
                        UUID userId,
                        Pageable pageable);

        Optional<Transaction> findByIdAndUserId(
                        Long id,
                        UUID userId);

        List<Transaction> findByUserIdAndDateBetweenOrderByDateDesc(
                        UUID userId,
                        LocalDate startDate,
                        LocalDate endDate);

        List<Transaction> findByUserIdAndCategoryIgnoreCaseAndType(
                        UUID userId,
                        String category,
                        TransactionType type);

        List<Transaction> findByUserIdAndType(
                        UUID userId,
                        TransactionType type);

        List<Transaction> findByUserIdAndDateBetween(
                        UUID userId,
                        LocalDate startDate,
                        LocalDate endDate);

        List<Transaction> findByUserIdAndCategoryIgnoreCase(
                        UUID userId,
                        String category);

        boolean existsByIdAndUserId(
                        Long id,
                        UUID userId);

        long countByUserIdAndType(
                        UUID userId,
                        TransactionType type);

        long countByUserId(
                        UUID userId);
}