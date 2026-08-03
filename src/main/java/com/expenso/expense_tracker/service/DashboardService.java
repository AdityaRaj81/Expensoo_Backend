package com.expenso.expense_tracker.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expenso.expense_tracker.dto.dashboard.CategoryDataDTO;
import com.expenso.expense_tracker.dto.dashboard.DashboardResponse;
import com.expenso.expense_tracker.dto.dashboard.MonthlyDataDTO;
import com.expenso.expense_tracker.dto.transaction.TransactionDTO;
import com.expenso.expense_tracker.enums.TransactionType;
import com.expenso.expense_tracker.exception.ResourceNotFoundException;
import com.expenso.expense_tracker.mapper.TransactionMapper;
import com.expenso.expense_tracker.model.Transaction;
import com.expenso.expense_tracker.repository.TransactionRepository;
import com.expenso.expense_tracker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * ============================================================
 * Dashboard Service
 * ============================================================
 *
 * Handles
 *
 * • Dashboard Summary
 * • Monthly Income
 * • Monthly Expense
 * • Monthly Balance
 * • Recent Transactions
 * • Monthly Overview
 * • Expense Breakdown
 *
 * ============================================================
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    /**
     * ============================================================
     * Get Dashboard
     * ============================================================
     */
    public DashboardResponse getDashboard(UUID userId) {
        return getDashboard(
                userId,
                YearMonth.now()
        );
    }
    /**
     * ============================================================
     * Get Dashboard (Selected Month)
     * ============================================================
     */
    public DashboardResponse getDashboard(
            UUID userId,
            YearMonth selectedMonth
    ) {
        userRepository
                .findByIdAndActiveTrue(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."
                        )
                );
        List<Transaction> allTransactions =
                transactionRepository
                        .findByUserIdOrderByDateDesc(userId);
        List<Transaction> currentMonthTransactions =
                getCurrentMonthTransactions(
                        allTransactions,
                        selectedMonth
                );
        BigDecimal monthlyIncome =
                calculateMonthlyIncome(
                        currentMonthTransactions
                );
        BigDecimal monthlyExpense =
                calculateMonthlyExpense(
                        currentMonthTransactions
                );
        BigDecimal monthlyBalance =
                monthlyIncome.subtract(
                        monthlyExpense
                );
        List<TransactionDTO> recentTransactions =
                transactionMapper.toTransactionDTOList(
                        allTransactions.stream()
                                .limit(5)
                                .collect(Collectors.toList())
                );
        return DashboardResponse.builder()
                .monthlyIncome(monthlyIncome)
                .monthlyExpense(monthlyExpense)
                .monthlyBalance(monthlyBalance)
                .recentTransactions(recentTransactions)
                .monthlyOverview(
                        generateMonthlyOverview(
                                allTransactions
                        )
                )
                .expenseBreakdown(
                        generateExpenseBreakdown(
                                currentMonthTransactions
                        )
                )
                .build();
    }
    /**
     * ============================================================
     * Current Month Transactions
     * ============================================================
     */
    private List<Transaction> getCurrentMonthTransactions(
            List<Transaction> transactions,
            YearMonth month
    ) {
        return transactions.stream()
                .filter(transaction ->
                        YearMonth.from(
                                transaction.getDate()
                        ).equals(month)
                )
                .collect(Collectors.toList());
    }
    /**
     * ============================================================
     * Monthly Income
     * ============================================================
     */
    private BigDecimal calculateMonthlyIncome(
            List<Transaction> transactions
    ) {
        return transactions.stream()
                .filter(transaction ->
                        transaction.getType()
                                == TransactionType.INCOME
                )
                .map(Transaction::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }
    /**
     * ============================================================
     * Monthly Expense
     * ============================================================
     */
    private BigDecimal calculateMonthlyExpense(
            List<Transaction> transactions
    ) {
        return transactions.stream()
                .filter(transaction ->
                        transaction.getType()
                                == TransactionType.EXPENSE
                )
                .map(Transaction::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }
    /**
     * ============================================================
     * Monthly Overview
     * ============================================================
     *
     * Generates the last 6 months' Income, Expense and Balance.
     */
    private List<MonthlyDataDTO> generateMonthlyOverview(
            List<Transaction> transactions
    ) {
        List<MonthlyDataDTO> monthlyOverview = new java.util.ArrayList<>();
        YearMonth currentMonth = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            BigDecimal income = BigDecimal.ZERO;
            BigDecimal expense = BigDecimal.ZERO;
            for (Transaction transaction : transactions) {
                if (!YearMonth.from(transaction.getDate()).equals(month)) {
                    continue;
                }
                if (transaction.getType() == TransactionType.INCOME) {
                    income = income.add(
                            transaction.getAmount()
                    );
                } else {
                    expense = expense.add(
                            transaction.getAmount()
                    );
                }
            }
            monthlyOverview.add(
                    MonthlyDataDTO.builder()
                            .month(
                                    month.getMonth().name()
                            )
                            .income(income)
                            .expense(expense)
                            .balance(
                                    income.subtract(expense)
                            )
                            .build()
            );
        }
        return monthlyOverview;
    }
    /**
     * ============================================================
     * Expense Breakdown
     * ============================================================
     *
     * Groups expenses by category for the selected month.
     */
    private List<CategoryDataDTO> generateExpenseBreakdown(
            List<Transaction> transactions
    ) {
        return transactions.stream()
                .filter(transaction ->
                        transaction.getType()
                                == TransactionType.EXPENSE
                )
                .collect(
                        Collectors.groupingBy(
                                Transaction::getCategory,
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        Transaction::getAmount,
                                        BigDecimal::add
                                )
                        )
                )
                .entrySet()
                .stream()
                .map(entry ->
                        CategoryDataDTO.builder()
                                .category(
                                        entry.getKey()
                                )
                                .amount(
                                        entry.getValue()
                                )
                                .build()
                )
                .sorted((a, b) ->
                        b.getAmount().compareTo(
                                a.getAmount()
                        )
                )
                .toList();
    }
    /**
     * ============================================================
     * Current Month
     * ============================================================
     */
    private YearMonth getCurrentMonth() {
        return YearMonth.now();
    }
}