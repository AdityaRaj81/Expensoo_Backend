package com.expenso.expense_tracker.controller;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.expenso.expense_tracker.dto.common.ApiResponse;
import com.expenso.expense_tracker.dto.transaction.TransactionDTO;
import com.expenso.expense_tracker.dto.transaction.TransactionRequest;
import com.expenso.expense_tracker.dto.transaction.TransactionResponse;
import com.expenso.expense_tracker.enums.TransactionType;
import com.expenso.expense_tracker.exception.BadRequestException;
import com.expenso.expense_tracker.security.JwtService;
import com.expenso.expense_tracker.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

        private static final int MAX_PAGE_SIZE = 100;

        private static final Set<String> ALLOWED_DATE_SORTS = Set.of("asc", "desc");

        private static final Set<String> ALLOWED_AMOUNT_SORTS = Set.of("asc", "desc");

        private final TransactionService transactionService;

        private final JwtService jwtService;

        private UUID extractUserId(
                        String authorizationHeader) {
                return jwtService.extractUserId(
                                authorizationHeader);
        }

        @PostMapping
        public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(

                        @RequestHeader("Authorization") String authorizationHeader,

                        @Valid @RequestBody TransactionRequest request

        ) {

                UUID userId = extractUserId(
                                authorizationHeader);

                TransactionResponse response = transactionService.createTransaction(
                                userId,
                                request);

                ApiResponse<TransactionResponse> apiResponse = ApiResponse
                                .<TransactionResponse>builder()
                                .success(true)
                                .message(
                                                "Transaction created successfully.")
                                .data(response)
                                .build();

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(apiResponse);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(

                        @RequestHeader("Authorization") String authorizationHeader,

                        @PathVariable Long id

        ) {

                UUID userId = extractUserId(
                                authorizationHeader);

                TransactionResponse response = transactionService.getTransactionById(
                                userId,
                                id);

                ApiResponse<TransactionResponse> apiResponse = ApiResponse
                                .<TransactionResponse>builder()
                                .success(true)
                                .message(
                                                "Transaction fetched successfully.")
                                .data(response)
                                .build();

                return ResponseEntity.ok(
                                apiResponse);
        }

        @GetMapping
        public ResponseEntity<ApiResponse<Page<TransactionDTO>>> getTransactions(

                        @RequestHeader("Authorization") String authorizationHeader,

                        @RequestParam(required = false) String search,

                        @RequestParam(required = false) TransactionType type,

                        @RequestParam(required = false) String category,

                        @RequestParam(required = false) LocalDate fromDate,

                        @RequestParam(required = false) LocalDate toDate,

                        @RequestParam(defaultValue = "desc") String dateSort,

                        @RequestParam(required = false) String amountSort,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "20") int size

        ) {

                UUID userId = extractUserId(
                                authorizationHeader);

                validatePagination(
                                page,
                                size);

                String normalizedDateSort = normalizeDateSort(
                                dateSort);

                String normalizedAmountSort = normalizeAmountSort(
                                amountSort);

                Sort.Direction dateDirection = "asc".equals(normalizedDateSort)
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC;

                Sort sort = Sort.by(
                                new Sort.Order(
                                                dateDirection,
                                                "date"));

                if (normalizedAmountSort != null) {

                        Sort.Direction amountDirection = "asc".equals(normalizedAmountSort)
                                        ? Sort.Direction.ASC
                                        : Sort.Direction.DESC;

                        sort = sort.and(
                                        Sort.by(
                                                        new Sort.Order(
                                                                        amountDirection,
                                                                        "amount")));
                }

                sort = sort.and(
                                Sort.by(
                                                Sort.Order.desc("id")));

                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                sort);

                Page<TransactionDTO> response = transactionService.getTransactions(
                                userId,
                                search,
                                type,
                                category,
                                fromDate,
                                toDate,
                                pageable);

                ApiResponse<Page<TransactionDTO>> apiResponse = ApiResponse
                                .<Page<TransactionDTO>>builder()
                                .success(true)
                                .message(
                                                "Transactions fetched successfully.")
                                .data(response)
                                .build();

                return ResponseEntity.ok(
                                apiResponse);
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(

                        @RequestHeader("Authorization") String authorizationHeader,

                        @PathVariable Long id,

                        @Valid @RequestBody TransactionRequest request

        ) {

                UUID userId = extractUserId(
                                authorizationHeader);

                TransactionResponse response = transactionService.updateTransaction(
                                id,
                                request,
                                userId);

                ApiResponse<TransactionResponse> apiResponse = ApiResponse
                                .<TransactionResponse>builder()
                                .success(true)
                                .message(
                                                "Transaction updated successfully.")
                                .data(response)
                                .build();

                return ResponseEntity.ok(
                                apiResponse);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> deleteTransaction(

                        @RequestHeader("Authorization") String authorizationHeader,

                        @PathVariable Long id

        ) {

                UUID userId = extractUserId(
                                authorizationHeader);

                transactionService.deleteTransaction(
                                id,
                                userId);

                ApiResponse<Void> apiResponse = ApiResponse
                                .<Void>builder()
                                .success(true)
                                .message(
                                                "Transaction deleted successfully.")
                                .data(null)
                                .build();

                return ResponseEntity.ok(
                                apiResponse);
        }

        private void validatePagination(
                        int page,
                        int size) {

                if (page < 0) {
                        throw new BadRequestException(
                                        "Page cannot be negative.");
                }

                if (size < 1 ||
                                size > MAX_PAGE_SIZE) {
                        throw new BadRequestException(
                                        "Page size must be between 1 and 100.");
                }
        }

        private String normalizeDateSort(
                        String dateSort) {

                if (dateSort == null ||
                                dateSort.isBlank()) {
                        return "desc";
                }

                String normalized = dateSort
                                .strip()
                                .toLowerCase();

                if (!ALLOWED_DATE_SORTS.contains(
                                normalized)) {
                        throw new BadRequestException(
                                        "Date sort must be asc or desc.");
                }

                return normalized;
        }

        private String normalizeAmountSort(
                        String amountSort) {

                if (amountSort == null ||
                                amountSort.isBlank()) {
                        return null;
                }

                String normalized = amountSort
                                .strip()
                                .toLowerCase();

                if (!ALLOWED_AMOUNT_SORTS.contains(
                                normalized)) {
                        throw new BadRequestException(
                                        "Amount sort must be asc or desc.");
                }

                return normalized;
        }
}