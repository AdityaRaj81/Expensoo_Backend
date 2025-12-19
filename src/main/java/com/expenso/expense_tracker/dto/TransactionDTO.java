package com.expenso.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TransactionDTO {
    private String id;
    private double amount;
    private String category;
    private String type;
    private LocalDate date;
    private String notes;
}
