package com.expenso.expense_tracker.dto.dashboard;

import com.expenso.expense_tracker.dto.transaction.TransactionDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dashboard API Response.
 *
 * Everything required by the Dashboard screen
 * should come from this DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    /**
     * Current Month Income
     */
    private BigDecimal monthlyIncome;

    private BigDecimal monthlyExpense;

    private BigDecimal monthlyBalance;

    /**
     * Recent Transactions
     */
    @Builder.Default
    private List<TransactionDTO> recentTransactions = List.of();

    /**
     * Monthly Chart Data
     */
    @Builder.Default
    private List<MonthlyDataDTO> monthlyOverview = List.of();

    /**
     * Expense Categories
     */
    @Builder.Default
    private List<CategoryDataDTO> expenseBreakdown = List.of();

}