package com.expenso.expense_tracker.model;

import com.expenso.expense_tracker.enums.TransactionType;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import jakarta.validation.constraints.Size;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions", indexes = {

                @Index(name = "idx_transactions_user_date", columnList = "userId,date"),

                @Index(name = "idx_transactions_user_type_date", columnList = "userId,type,date"),

                @Index(name = "idx_transactions_user_category", columnList = "userId,category")

})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private UUID userId;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private TransactionType type;

        @NotNull
        @Positive
        @Column(nullable = false, precision = 12, scale = 2)
        private BigDecimal amount;

        @NotBlank
        @Column(nullable = false, length = 100)
        private String category;

        @NotNull
        @Column(nullable = false)
        private LocalDate date;

        @Size(max = 500)
        @Column(length = 500)
        private String notes;

        @CreationTimestamp
        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        private LocalDateTime updatedAt;

}