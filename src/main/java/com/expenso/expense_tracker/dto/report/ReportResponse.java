package com.expenso.expense_tracker.dto.report;

import java.math.BigDecimal;
import java.util.List;

import com.expenso.expense_tracker.dto.transaction.TransactionDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ============================================================
 * Report Response DTO
 * ============================================================
 *
 * Used for Monthly Report,
 * Date Range Report,
 * Export Report.
 *
 * ============================================================
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    /**
     * Report Title
     *
     * Example:
     * July 2026 Report
     */
    private String title;

    /**
     * Total Income
     */
    private BigDecimal totalIncome;

    /**
     * Total Expense
     */
    private BigDecimal totalExpense;

    /**
     * Net Balance
     */
    private BigDecimal balance;

    /**
     * Transactions Included
     */
    private List<TransactionDTO> transactions;

}