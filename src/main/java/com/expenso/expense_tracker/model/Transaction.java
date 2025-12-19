package com.expenso.expense_tracker.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;

    private String type; // INCOME or EXPENSE
    private Double amount;

    private String category;
    private LocalDate date;
    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
}
