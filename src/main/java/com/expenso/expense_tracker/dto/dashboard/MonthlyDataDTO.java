package com.expenso.expense_tracker.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Monthly summary used for dashboard charts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyDataDTO {

    /**
     * Month Name
     * Example: Jan, Feb, Mar
     */
    private String month;

    /**
     * Total Income
     */
    private BigDecimal income;

    /**
     * Total Expense
     */
    private BigDecimal expense;

    /**
     * Remaining Balance
     */
    private BigDecimal balance;

}