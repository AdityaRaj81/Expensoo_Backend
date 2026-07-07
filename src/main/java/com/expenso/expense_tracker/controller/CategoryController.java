package com.expenso.expense_tracker.controller;

import com.expenso.expense_tracker.model.Transaction;
import com.expenso.expense_tracker.repository.TransactionRepository;
import com.expenso.expense_tracker.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final TransactionRepository transactionRepository;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getCategories(@RequestHeader("Authorization") String authHeader) {
        try {
            UUID userId = jwtService.extractUserId(authHeader);
            List<Transaction> transactions = transactionRepository.findByUserId(userId);

            List<String> income = categoriesByType(transactions, "income");
            List<String> expense = categoriesByType(transactions, "expense");
            List<String> all = transactions.stream()
                    .map(Transaction::getCategory)
                    .filter(category -> category != null && !category.isBlank())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "income", income,
                    "expense", expense,
                    "all", all));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to fetch categories");
        }
    }

    private List<String> categoriesByType(List<Transaction> transactions, String type) {
        return transactions.stream()
                .filter(transaction -> transaction.getType() != null && transaction.getType().equalsIgnoreCase(type))
                .map(Transaction::getCategory)
                .filter(category -> category != null && !category.isBlank())
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }
}
