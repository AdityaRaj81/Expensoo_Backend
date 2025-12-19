package com.expenso.expense_tracker.service;

import com.expenso.expense_tracker.dto.TransactionDTO;
import com.expenso.expense_tracker.dto.TransactionRequest;
import com.expenso.expense_tracker.model.Transaction;
import com.expenso.expense_tracker.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

        private final TransactionRepository transactionRepository;

        public List<TransactionDTO> getAllTransactions(UUID userId) {
                System.out.println("DEBUG TransactionService: Fetching all transactions for userId: " + userId);
                List<Transaction> transactions = transactionRepository.findByUserId(userId);
                System.out.println("DEBUG TransactionService: Found " + transactions.size() + " transactions");

                return transactions.stream().map(t -> new TransactionDTO(
                                String.valueOf(t.getId()),
                                t.getAmount(),
                                t.getCategory(),
                                t.getType(),
                                t.getDate())).collect(Collectors.toList());
        }

        public Map<String, Object> getPaginatedTransactions(UUID userId, int page, int limit, String sortBy,
                        String sortOrder) {
                System.out.println("DEBUG TransactionService: Fetching paginated transactions for userId: " + userId +
                                ", page: " + page + ", limit: " + limit + ", sortBy: " + sortBy + ", sortOrder: "
                                + sortOrder);

                sortBy = (sortBy == null || sortBy.trim().isEmpty()) ? "date" : sortBy;
                sortOrder = (sortOrder == null || sortOrder.trim().isEmpty()) ? "desc" : sortOrder;

                Sort sort = Sort.by(Sort.Direction.fromString(sortOrder.toUpperCase()), sortBy);
                PageRequest pageable = PageRequest.of(page - 1, limit, sort);

                Page<Transaction> transactionPage = transactionRepository.findByUserId(userId, pageable);
                System.out.println("DEBUG TransactionService: Found " + transactionPage.getTotalElements()
                                + " total transactions");

                List<TransactionDTO> transactionDTOs = transactionPage.getContent().stream()
                                .map(t -> new TransactionDTO(
                                                String.valueOf(t.getId()),
                                                t.getAmount(),
                                                t.getCategory(),
                                                t.getType(),
                                                t.getDate()))
                                .collect(Collectors.toList());

                Map<String, Object> response = new HashMap<>();
                response.put("transactions", transactionDTOs);

                Map<String, Object> pagination = new HashMap<>();
                pagination.put("page", transactionPage.getNumber() + 1);
                pagination.put("limit", transactionPage.getSize());
                pagination.put("total", transactionPage.getTotalElements());
                pagination.put("totalPages", transactionPage.getTotalPages());

                response.put("pagination", pagination);
                return response;
        }

        public Map<String, Object> getFilteredTransactions(
                        UUID userId,
                        String search,
                        String type,
                        String category,
                        LocalDate dateFrom,
                        LocalDate dateTo,
                        int page,
                        int limit,
                        String sortBy,
                        String sortOrder) {
                sortBy = (sortBy == null || sortBy.trim().isEmpty()) ? "date" : sortBy;
                sortOrder = (sortOrder == null || sortOrder.trim().isEmpty()) ? "desc" : sortOrder;

                Sort sort = Sort.by(Sort.Direction.fromString(sortOrder.toUpperCase()), sortBy);
                PageRequest pageable = PageRequest.of(page - 1, limit, sort);

                Specification<Transaction> spec = Specification
                                .where((root, query, cb) -> cb.equal(root.get("userId"), userId));

                if (type != null && !type.isBlank()) {
                        spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
                }

                if (category != null && !category.isBlank()) {
                        spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
                }

                // if (search != null && !search.isEmpty()) {
                // spec = spec.and((root, query, cb) ->
                // cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() +
                // "%"));
                // }

                // if (type != null && !type.isEmpty()) {
                // spec = spec.and((root, query, cb) ->
                // cb.equal(root.get("type"), type));
                // }

                // if (category != null && !category.isEmpty()) {
                // spec = spec.and((root, query, cb) ->
                // cb.equal(root.get("category"), category));
                // }

                if (dateFrom != null) {
                        spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), dateFrom));
                }

                if (dateTo != null) {
                        spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), dateTo));
                }

                Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

                List<TransactionDTO> transactionDTOs = transactionPage.getContent().stream()
                                .map(t -> new TransactionDTO(
                                                String.valueOf(t.getId()),

                                                t.getAmount(),
                                                t.getCategory(),
                                                t.getType(),
                                                t.getDate()))
                                .collect(Collectors.toList());

                Map<String, Object> response = new HashMap<>();
                response.put("transactions", transactionDTOs);

                Map<String, Object> pagination = new HashMap<>();
                pagination.put("page", transactionPage.getNumber() + 1);
                pagination.put("limit", transactionPage.getSize());
                pagination.put("total", transactionPage.getTotalElements());
                pagination.put("totalPages", transactionPage.getTotalPages());

                response.put("pagination", pagination);
                return response;
        }

        public Transaction addTransaction(TransactionRequest request, UUID userId) {
                System.out.println("DEBUG TransactionService: Adding transaction for userId: " + userId);
                System.out.println("DEBUG TransactionService: Request - type: " + request.getType() +
                                ", amount: " + request.getAmount() +
                                ", category: " + request.getCategory() +
                                ", date: " + request.getDate() +
                                ", notes: " + request.getNotes());

                Transaction transaction = Transaction.builder()
                                .userId(userId)
                                .type(request.getType())
                                .amount(request.getAmount())
                                .category(request.getCategory())
                                .date(request.getDate())
                                .notes(request.getNotes())
                                .build();

                Transaction saved = transactionRepository.save(transaction);
                System.out.println("DEBUG TransactionService: Transaction saved with ID: " + saved.getId());
                return saved;
        }

        public Transaction updateTransaction(Long id, TransactionRequest request, UUID userId) {
                Transaction existing = transactionRepository.findByIdAndUserId(id, userId)
                                .orElseThrow(() -> new RuntimeException("Transaction not found or unauthorized"));

                existing.setAmount(request.getAmount());
                existing.setCategory(request.getCategory());
                existing.setDate(request.getDate());

                existing.setNotes(request.getNotes());
                existing.setType(request.getType());

                return transactionRepository.save(existing);
        }

        public void deleteTransaction(Long id, UUID userId) {
                Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                                .orElseThrow(() -> new RuntimeException("Transaction not found or unauthorized"));

                transactionRepository.delete(transaction);
        }
}
