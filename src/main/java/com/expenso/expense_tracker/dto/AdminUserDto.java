package com.expenso.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserDto {
  private UUID id;
  private String email;
  private String name;
  private String role;
  private Boolean active;
  private LocalDateTime createdAt;
}
