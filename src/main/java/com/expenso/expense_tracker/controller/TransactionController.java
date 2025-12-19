package com.expenso.expense_tracker.controller;

import com.expenso.expense_tracker.model.Transaction;
import com.expenso.expense_tracker.dto.TransactionRequest;
import com.expenso.expense_tracker.security.JwtService;
import com.expenso.expense_tracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    private JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getTransactions(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }

            UUID userId = jwtService.extractUserId(authHeader);
            return ResponseEntity.ok(transactionService.getPaginatedTransactions(userId, page, limit, sortBy, sortOrder));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token. Please login again.");
        }
    }

    @GetMapping("/filter")
public ResponseEntity<?> getFilteredTransactions(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(defaultValue = "date") String sortBy,
        @RequestParam(defaultValue = "desc") String sortOrder
) {
    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        UUID userId = jwtService.extractUserId(authHeader);

        return ResponseEntity.ok(transactionService.getFilteredTransactions(
                userId, search, type, category, dateFrom, dateTo, page, limit, sortBy, sortOrder
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch filtered transactions");
    }
}

    @PostMapping("/add")
    public ResponseEntity<?> addTransaction(
            @RequestBody TransactionRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }

            UUID userId = jwtService.extractUserId(authHeader);
            Transaction transaction = transactionService.addTransaction(request, userId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token. Please login again.");
        }
    }

    // UPDATE Transaction
@PutMapping("/{id}")
public ResponseEntity<?> updateTransaction(
        @PathVariable Long  id,
        @RequestBody TransactionRequest request,
        @RequestHeader("Authorization") String authHeader
) {
    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        UUID userId = jwtService.extractUserId(authHeader);
        Transaction updatedTransaction = transactionService.updateTransaction(id, request, userId);
        return ResponseEntity.ok(updatedTransaction);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update transaction");
    }
}

// DELETE Transaction
@DeleteMapping("/{id}")
public ResponseEntity<?> deleteTransaction(
        @PathVariable Long  id,
        @RequestHeader("Authorization") String authHeader
) {
    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        UUID userId = jwtService.extractUserId(authHeader);
        transactionService.deleteTransaction(id, userId);
        return ResponseEntity.ok("Transaction deleted successfully");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete transaction");
    }
}
}
