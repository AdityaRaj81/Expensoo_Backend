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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    private JwtService jwtService;

    private UUID getUserIdFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return jwtService.extractUserId(authHeader);
    }

    @GetMapping
    public ResponseEntity<?> getTransactions(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }

            UUID userId = jwtService.extractUserId(authHeader);
            System.out.println("DEBUG: Fetching transactions for userId: " + userId);
            return ResponseEntity
                    .ok(transactionService.getPaginatedTransactions(userId, page, limit, sortBy, sortOrder));
        } catch (Exception e) {
            System.err.println("DEBUG: Error fetching transactions: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token. Please login again. Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            UUID userId = getUserIdFromAuthHeader(authHeader);
            return ResponseEntity.ok(transactionService.getTransaction(id, userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
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
            @RequestParam(defaultValue = "desc") String sortOrder) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }

            UUID userId = jwtService.extractUserId(authHeader);
            System.out.println("DEBUG: Fetching filtered transactions for userId: " + userId);
            return ResponseEntity.ok(transactionService.getFilteredTransactions(
                    userId, search, type, category, dateFrom, dateTo, page, limit, sortBy, sortOrder));
        } catch (Exception e) {
            System.err.println("DEBUG: Error fetching filtered transactions: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch filtered transactions. Error: " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportTransactions(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        try {
            UUID userId = getUserIdFromAuthHeader(authHeader);
            List<Transaction> transactions = transactionService.getTransactionsForExport(
                    userId, type, category, dateFrom, dateTo);

            StringBuilder csv = new StringBuilder();
            csv.append("Date,Type,Category,Amount,Notes\n");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Transaction transaction : transactions) {
                csv.append(transaction.getDate().format(formatter)).append(",")
                        .append(escapeCsv(transaction.getType())).append(",")
                        .append(escapeCsv(transaction.getCategory())).append(",")
                        .append(transaction.getAmount()).append(",")
                        .append(escapeCsv(transaction.getNotes()))
                        .append("\n");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "transactions_"
                    + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv");

            return new ResponseEntity<>(csv.toString(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to export transactions");
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTransaction(
            @RequestBody TransactionRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }

            UUID userId = jwtService.extractUserId(authHeader);
            System.out.println("DEBUG: Adding transaction for userId: " + userId);
            System.out.println("DEBUG: Transaction request: " + request);
            Transaction transaction = transactionService.addTransaction(request, userId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            System.err.println("DEBUG: Error adding transaction: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to add transaction. Error: " + e.getMessage());
        }
    }

    // UPDATE Transaction
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionRequest request,
            @RequestHeader("Authorization") String authHeader) {
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
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
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
