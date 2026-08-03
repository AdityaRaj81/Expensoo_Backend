package com.expenso.expense_tracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expenso.expense_tracker.dto.transaction.TransactionDTO;
import com.expenso.expense_tracker.dto.transaction.TransactionRequest;
import com.expenso.expense_tracker.dto.transaction.TransactionResponse;
import com.expenso.expense_tracker.enums.TransactionType;
import com.expenso.expense_tracker.exception.BadRequestException;
import com.expenso.expense_tracker.exception.ResourceNotFoundException;
import com.expenso.expense_tracker.mapper.TransactionMapper;
import com.expenso.expense_tracker.model.Transaction;
import com.expenso.expense_tracker.repository.TransactionRepository;
import com.expenso.expense_tracker.specification.TransactionSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

        private final TransactionRepository transactionRepository;

        private final TransactionMapper transactionMapper;

        public TransactionResponse createTransaction(
                        UUID userId,
                        TransactionRequest request) {
                validateTransactionRequest(request);

                Transaction transaction = Transaction.builder()
                                .userId(userId)
                                .type(request.getType())
                                .amount(request.getAmount())
                                .category(
                                                normalizeCategory(
                                                                request.getCategory()))
                                .date(request.getDate())
                                .notes(
                                                normalizeNotes(
                                                                request.getNotes()))
                                .build();

                Transaction savedTransaction = transactionRepository.save(
                                transaction);

                return transactionMapper.toTransactionResponse(
                                savedTransaction);
        }

        @Transactional(readOnly = true)
        public TransactionResponse getTransactionById(
                        UUID userId,
                        Long transactionId) {
                Transaction transaction = getTransactionEntity(
                                transactionId,
                                userId);

                return transactionMapper.toTransactionResponse(
                                transaction);
        }

        @Transactional(readOnly = true)
        public List<TransactionDTO> getAllTransactions(
                        UUID userId) {
                List<Transaction> transactions = transactionRepository
                                .findByUserIdOrderByDateDesc(
                                                userId);

                return transactionMapper.toTransactionDTOList(
                                transactions);
        }

        @Transactional(readOnly = true)
        public Page<TransactionDTO> getTransactions(
                        UUID userId,
                        String search,
                        TransactionType type,
                        String category,
                        LocalDate fromDate,
                        LocalDate toDate,
                        Pageable pageable) {
                if (fromDate != null &&
                                toDate != null &&
                                fromDate.isAfter(toDate)) {
                        throw new BadRequestException(
                                        "From date cannot be after to date.");
                }

                Specification<Transaction> specification = Specification.where(
                                TransactionSpecification
                                                .belongsToUser(userId));

                if (search != null &&
                                !search.isBlank()) {
                        specification = specification.and(
                                        TransactionSpecification
                                                        .containsSearch(search));
                }

                if (type != null) {
                        specification = specification.and(
                                        TransactionSpecification
                                                        .hasType(type));
                }

                if (category != null &&
                                !category.isBlank()) {
                        specification = specification.and(
                                        TransactionSpecification
                                                        .hasCategory(category));
                }

                if (fromDate != null) {
                        specification = specification.and(
                                        TransactionSpecification
                                                        .fromDate(fromDate));
                }

                if (toDate != null) {
                        specification = specification.and(
                                        TransactionSpecification
                                                        .toDate(toDate));
                }

                Page<Transaction> transactionPage = transactionRepository.findAll(
                                specification,
                                pageable);

                return transactionPage.map(
                                transactionMapper::toTransactionDTO);
        }

        public TransactionResponse updateTransaction(
                        Long transactionId,
                        TransactionRequest request,
                        UUID userId) {
                validateTransactionRequest(request);

                Transaction transaction = getTransactionEntity(
                                transactionId,
                                userId);

                transaction.setType(
                                request.getType());

                transaction.setAmount(
                                request.getAmount());

                transaction.setCategory(
                                normalizeCategory(
                                                request.getCategory()));

                transaction.setDate(
                                request.getDate());

                transaction.setNotes(
                                normalizeNotes(
                                                request.getNotes()));

                Transaction updatedTransaction = transactionRepository.save(
                                transaction);

                return transactionMapper.toTransactionResponse(
                                updatedTransaction);
        }

        public void deleteTransaction(
                        Long transactionId,
                        UUID userId) {
                Transaction transaction = getTransactionEntity(
                                transactionId,
                                userId);

                transactionRepository.delete(
                                transaction);
        }

        @Transactional(readOnly = true)
        public List<TransactionDTO> getTransactionsByCategory(
                        UUID userId,
                        String category) {
                if (category == null ||
                                category.isBlank()) {
                        throw new BadRequestException(
                                        "Category is required.");
                }

                List<Transaction> transactions = transactionRepository
                                .findByUserIdAndCategoryIgnoreCase(
                                                userId,
                                                normalizeCategory(category));

                return transactionMapper.toTransactionDTOList(
                                transactions);
        }

        @Transactional(readOnly = true)
        public List<TransactionDTO> getTransactionsByType(
                        UUID userId,
                        TransactionType type) {
                if (type == null) {
                        throw new BadRequestException(
                                        "Transaction type is required.");
                }

                List<Transaction> transactions = transactionRepository
                                .findByUserIdAndType(
                                                userId,
                                                type);

                return transactionMapper.toTransactionDTOList(
                                transactions);
        }

        @Transactional(readOnly = true)
        public List<TransactionDTO> getTransactionsBetweenDates(
                        UUID userId,
                        LocalDate startDate,
                        LocalDate endDate) {
                if (startDate == null ||
                                endDate == null) {
                        throw new BadRequestException(
                                        "Start date and end date are required.");
                }

                if (startDate.isAfter(endDate)) {
                        throw new BadRequestException(
                                        "Start date cannot be after end date.");
                }

                List<Transaction> transactions = transactionRepository
                                .findByUserIdAndDateBetweenOrderByDateDesc(
                                                userId,
                                                startDate,
                                                endDate);

                return transactionMapper.toTransactionDTOList(
                                transactions);
        }

        @Transactional(readOnly = true)
        public long countTransactions(
                        UUID userId) {
                return transactionRepository
                                .countByUserId(
                                                userId);
        }

        @Transactional(readOnly = true)
        public boolean belongsToUser(
                        Long transactionId,
                        UUID userId) {
                return transactionRepository
                                .existsByIdAndUserId(
                                                transactionId,
                                                userId);
        }

        private Transaction getTransactionEntity(
                        Long transactionId,
                        UUID userId) {
                return transactionRepository
                                .findByIdAndUserId(
                                                transactionId,
                                                userId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Transaction not found."));
        }

        private void validateTransactionRequest(
                        TransactionRequest request) {
                if (request == null) {
                        throw new BadRequestException(
                                        "Transaction request cannot be null.");
                }

                if (request.getType() == null) {
                        throw new BadRequestException(
                                        "Transaction type is required.");
                }

                if (request.getAmount() == null ||
                                request.getAmount().signum() <= 0) {
                        throw new BadRequestException(
                                        "Amount must be greater than zero.");
                }

                if (request.getCategory() == null ||
                                request.getCategory().isBlank()) {
                        throw new BadRequestException(
                                        "Category is required.");
                }

                if (request.getDate() == null) {
                        throw new BadRequestException(
                                        "Transaction date is required.");
                }

                if (request.getCategory().strip().length() > 100) {
                        throw new BadRequestException(
                                        "Category cannot exceed 100 characters.");
                }

                if (request.getNotes() != null &&
                                request.getNotes().strip().length() > 500) {
                        throw new BadRequestException(
                                        "Notes cannot exceed 500 characters.");
                }
        }

        private String normalizeCategory(
                        String category) {
                return category
                                .strip()
                                .replaceAll(
                                                "\\s+",
                                                " ");
        }

        private String normalizeNotes(
                        String notes) {
                if (notes == null) {
                        return null;
                }

                String normalized = notes.strip();

                return normalized.isEmpty()
                                ? null
                                : normalized;
        }
}