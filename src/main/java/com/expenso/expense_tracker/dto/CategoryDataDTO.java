package com.expenso.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDataDTO {
    private String name;
    private double value;
}
