package com.expenso.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class DashboardResponse {
    private double totalIncome;
    private double totalExpenses;
    private double balance;
    private List<TransactionDTO> recentTransactions;
    private List<MonthlyDataDTO> monthlyData;
    private List<CategoryDataDTO> categoryData;
}
