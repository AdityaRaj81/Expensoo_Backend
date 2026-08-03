package com.expenso.expense_tracker.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Category-wise expense data.
 *
 * Used in Pie Chart / Doughnut Chart.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDataDTO {

    /**
     * Category Name
     * Example:
     * Food
     * Travel
     * Shopping
     */
    private String category;

    /**
     * Total Amount
     */
    private BigDecimal amount;

}