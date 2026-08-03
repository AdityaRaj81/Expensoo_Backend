package com.expenso.expense_tracker.dto.transaction;

import com.expenso.expense_tracker.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private Long id;

    private BigDecimal amount;

    private String category;

    private TransactionType type;

    private LocalDate date;

    private String notes;

}