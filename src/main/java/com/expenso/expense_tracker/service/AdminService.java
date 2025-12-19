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
    System.out.println("DEBUG AdminService: Fetching dashboard stats");
    long totalUsers = userRepository.count();
    System.out.println("DEBUG AdminService: Total users: " + totalUsers);

    long activeUsers = userRepository.findAll().stream()
        .filter(u -> u.getActive() && u.getCreatedAt().isAfter(LocalDateTime.now().minus(30, ChronoUnit.DAYS)))
        .count();
    System.out.println("DEBUG AdminService: Active users (last 30 days): " + activeUsers);

    long totalTransactions = transactionRepository.count();
    System.out.println("DEBUG AdminService: Total transactions: " + totalTransactions);

    Double totalIncome = transactionRepository.findAll().stream()
        .filter(t -> "INCOME".equals(t.getType()))
        .mapToDouble(Transaction::getAmount)
        .sum();

    Double totalExpense = transactionRepository.findAll().stream()
        .filter(t -> "EXPENSE".equals(t.getType()))
        .mapToDouble(Transaction::getAmount)
        .sum();

    System.out.println("DEBUG AdminService: Total income: " + totalIncome + ", Total expense: " + totalExpense);

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
    System.out.println("DEBUG AdminService: Fetching all users with search: " + search);
    List<User> users;

    if (search != null && !search.isEmpty()) {
      String searchLower = search.toLowerCase();
      users = userRepository.findAll().stream()
          .filter(u -> u.getEmail().toLowerCase().contains(searchLower) ||
              (u.getName() != null && u.getName().toLowerCase().contains(searchLower)))
          .collect(Collectors.toList());
      System.out.println("DEBUG AdminService: Found " + users.size() + " matching users");
    } else {
      users = userRepository.findAll();
      System.out.println("DEBUG AdminService: Found " + users.size() + " total users");
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
        dtos.size());
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
    System.out.println(
        "DEBUG AdminService: Fetching all transactions with filters - userEmail: " + userEmail + ", type: " + type);
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
      System.out.println("DEBUG AdminService: Found " + transactions.size() + " filtered transactions");
    } else {
      transactions = transactionRepository.findAll();
      System.out.println("DEBUG AdminService: Found " + transactions.size() + " total transactions");
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
        dtos.size());
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
