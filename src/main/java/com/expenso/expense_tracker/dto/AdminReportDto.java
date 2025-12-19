package com.expenso.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminReportDto {
  private String reportType; // "user-export", "transaction-export", "summary"
  private String title;
  private String description;
  private Boolean available;
}
