package com.expenso.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatsDto {
  private Long totalUsers;
  private Long activeUsers;
  private Long totalTransactions;
  private Double totalIncome;
  private Double totalExpense;
  private String backendStatus; // "Operational", "Degraded", etc.
  private String databaseStatus;
  private LocalDateTime lastChecked;
}
