package com.expenso.expense_tracker.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expenso.expense_tracker.dto.report.ReportResponse;
import com.expenso.expense_tracker.dto.transaction.TransactionDTO;
import com.expenso.expense_tracker.enums.TransactionType;
import com.expenso.expense_tracker.mapper.TransactionMapper;
import com.expenso.expense_tracker.model.Transaction;
import com.expenso.expense_tracker.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

/**
 * ============================================================
 * Report Service
 * ============================================================
 *
 * Handles
 *
 * • Monthly Report
 * • Date Range Report
 * • Summary Report
 *
 * ============================================================
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    /**
     * ============================================================
     * Monthly Report
     * ============================================================
     */
    public ReportResponse getMonthlyReport(

            UUID userId,

            YearMonth month

    ) {

        LocalDate startDate = month.atDay(1);

        LocalDate endDate = month.atEndOfMonth();

        return getReport(

                userId,

                startDate,

                endDate,

                month.getMonth().name() + " " + month.getYear() + " Report"

        );

    }

    /**
     * ============================================================
     * Custom Date Report
     * ============================================================
     */
    public ReportResponse getReport(

            UUID userId,

            LocalDate startDate,

            LocalDate endDate,

            String title

    ) {

        List<Transaction> transactions =

                transactionRepository

                        .findByUserIdAndDateBetweenOrderByDateDesc(

                                userId,

                                startDate,

                                endDate

                        );

        BigDecimal totalIncome = transactions.stream()

                .filter(transaction ->

                        transaction.getType()

                                == TransactionType.INCOME

                )

                .map(Transaction::getAmount)

                .reduce(

                        BigDecimal.ZERO,

                        BigDecimal::add

                );

        BigDecimal totalExpense = transactions.stream()

                .filter(transaction ->

                        transaction.getType()

                                == TransactionType.EXPENSE

                )

                .map(Transaction::getAmount)

                .reduce(

                        BigDecimal.ZERO,

                        BigDecimal::add

                );

        List<TransactionDTO> transactionDTOs =

                transactionMapper.toTransactionDTOList(

                        transactions

                );

        return ReportResponse.builder()

                .title(title)

                .totalIncome(totalIncome)

                .totalExpense(totalExpense)

                .balance(

                        totalIncome.subtract(

                                totalExpense

                        )

                )

                .transactions(transactionDTOs)

                .build();

    }

}