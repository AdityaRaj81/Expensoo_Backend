package com.expenso.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyDataDTO {
    private String month;
    private double income;
    private double expenses;
    private double balance;
}
