package com.expenso.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTransactionDto {
  private Long id;
  private UUID userId;
  private String userEmail;
  private String type;
  private Double amount;
  private String category;
  private LocalDate date;
  private String notes;
}
