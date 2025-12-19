package com.expenso.expense_tracker.dto;


//package com.expenso.expense_tracker.payload;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionRequest {
    private String type;
    private Double amount;

    private String category;
    private LocalDate date;
    private String notes;
}


