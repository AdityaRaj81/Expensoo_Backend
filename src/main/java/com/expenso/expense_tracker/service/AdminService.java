package com.expenso.expense_tracker.service;

import com.expenso.expense_tracker.dto.AdminStatsDto;
import com.expenso.expense_tracker.dto.AdminUserDto;
import com.expenso.expense_tracker.dto.AdminTransactionDto;
import com.expenso.expense_tracker.model.Transaction;
import com.expenso.expense_tracker.model.User;
import com.expenso.expense_tracker.repository.TransactionRepository;
import com.expenso.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Get dashboard statistics
     */
    public AdminStatsDto getDashboardStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findAll().stream()
                .filter(u -> u.getActive() && u.getCreatedAt().isAfter(LocalDateTime.now().minus(30, ChronoUnit.DAYS)))
                .count();
        long totalTransactions = transactionRepository.count();

        Double totalIncome = transactionRepository.findAll().stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        Double totalExpense = transactionRepository.findAll().stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        return AdminStatsDto.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalTransactions(totalTransactions)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .backendStatus("Operational")
                .databaseStatus("Operational")
                .lastChecked(LocalDateTime.now())
                .build();
    }

    /**
     * Get paginated list of all users with optional search
     */
    public Page<AdminUserDto> getAllUsers(String search, Pageable pageable) {
        List<User> users;

        if (search != null && !search.isEmpty()) {
            String searchLower = search.toLowerCase();
            users = userRepository.findAll().stream()
                    .filter(u -> u.getEmail().toLowerCase().contains(searchLower) ||
                            (u.getName() != null && u.getName().toLowerCase().contains(searchLower)))
                    .collect(Collectors.toList());
        } else {
            users = userRepository.findAll();
        }

        List<AdminUserDto> dtos = users.stream()
                .map(u -> AdminUserDto.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .name(u.getName())
                        .role(u.getRole())
                        .active(u.getActive())
                        .createdAt(u.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtos.size());

        return new PageImpl<>(
                dtos.subList(start, end),
                pageable,
                dtos.size()
        );
    }

    /**
     * Block/unblock a user (soft delete)
     */
    public AdminUserDto blockUnblockUser(String userId, Boolean active) {
        java.util.UUID uuid = java.util.UUID.fromString(userId);
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent blocking the admin
        if ("ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Cannot modify admin account");
        }

        user.setActive(active);
        userRepository.save(user);

        return AdminUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * Get paginated list of all transactions with optional filters
     */
    public Page<AdminTransactionDto> getAllTransactions(String userEmail, String type, Pageable pageable) {
        List<Transaction> transactions;

        if ((userEmail != null && !userEmail.isEmpty()) || (type != null && !type.isEmpty())) {
            transactions = transactionRepository.findAll().stream()
                    .filter(t -> {
                        boolean matches = true;
                        if (userEmail != null && !userEmail.isEmpty()) {
                            User user = userRepository.findById(t.getUserId()).orElse(null);
                            if (user == null || !user.getEmail().toLowerCase().contains(userEmail.toLowerCase())) {
                                matches = false;
                            }
                        }
                        if (type != null && !type.isEmpty() && !t.getType().equals(type)) {
                            matches = false;
                        }
                        return matches;
                    })
                    .collect(Collectors.toList());
        } else {
            transactions = transactionRepository.findAll();
        }

        List<AdminTransactionDto> dtos = transactions.stream()
                .map(t -> {
                    User user = userRepository.findById(t.getUserId()).orElse(null);
                    return AdminTransactionDto.builder()
                            .id(t.getId())
                            .userId(t.getUserId())
                            .userEmail(user != null ? user.getEmail() : "Unknown")
                            .type(t.getType())
                            .amount(t.getAmount())
                            .category(t.getCategory())
                            .date(t.getDate())
                            .notes(t.getNotes())
                            .build();
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtos.size());

        return new PageImpl<>(
                dtos.subList(start, end),
                pageable,
                dtos.size()
        );
    }

    /**
     * Check system health (stub)
     */
    public AdminStatsDto getSystemHealth() {
        return AdminStatsDto.builder()
                .backendStatus("Operational")
                .databaseStatus("Operational")
                .lastChecked(LocalDateTime.now())
                .build();
    }
}
