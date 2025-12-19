package com.expenso.expense_tracker.controller;

import com.expenso.expense_tracker.dto.AdminStatsDto;
import com.expenso.expense_tracker.dto.AdminUserDto;
import com.expenso.expense_tracker.dto.AdminTransactionDto;
import com.expenso.expense_tracker.security.JwtService;
import com.expenso.expense_tracker.security.RoleValidator;
import com.expenso.expense_tracker.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

  @Autowired
  private AdminService adminService;

  @Autowired
  private JwtService jwtService;

  /**
   * Helper method to extract and validate admin role from token
   */
  private void validateAdminAccess(String authHeader) {
    try {
      String role = jwtService.extractRole(authHeader);
      RoleValidator.requireAdmin(role);
    } catch (Exception e) {
      throw new RuntimeException("Unauthorized admin access: " + e.getMessage());
    }
  }

  /**
   * GET /api/admin/dashboard - Admin dashboard stats
   */
  @GetMapping("/dashboard")
  public ResponseEntity<?> getDashboard(@RequestHeader("Authorization") String authHeader) {
    try {
      validateAdminAccess(authHeader);
      AdminStatsDto stats = adminService.getDashboardStats();
      return ResponseEntity.ok(stats);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
    }
  }

  /**
   * GET /api/admin/users - Get all users (paginated)
   */
  @GetMapping("/users")
  public ResponseEntity<?> getUsers(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      validateAdminAccess(authHeader);
      Pageable pageable = PageRequest.of(page, size);
      Page<AdminUserDto> users = adminService.getAllUsers(search, pageable);
      return ResponseEntity.ok(users);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
    }
  }

  /**
   * PUT /api/admin/users/{userId}/toggle - Block/unblock user
   */
  @PutMapping("/users/{userId}/toggle")
  public ResponseEntity<?> toggleUserStatus(
      @RequestHeader("Authorization") String authHeader,
      @PathVariable String userId,
      @RequestBody Map<String, Boolean> body) {
    try {
      validateAdminAccess(authHeader);
      Boolean active = body.get("active");
      if (active == null) {
        return ResponseEntity.badRequest().body(Map.of("message", "active field required"));
      }
      AdminUserDto user = adminService.blockUnblockUser(userId, active);
      return ResponseEntity.ok(user);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
    }
  }

  /**
   * GET /api/admin/transactions - Get all transactions (paginated)
   */
  @GetMapping("/transactions")
  public ResponseEntity<?> getTransactions(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(required = false) String userEmail,
      @RequestParam(required = false) String type,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      validateAdminAccess(authHeader);
      Pageable pageable = PageRequest.of(page, size);
      Page<AdminTransactionDto> transactions = adminService.getAllTransactions(userEmail, type, pageable);
      return ResponseEntity.ok(transactions);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
    }
  }

  /**
   * GET /api/admin/health - System health check
   */
  @GetMapping("/health")
  public ResponseEntity<?> getHealth(@RequestHeader("Authorization") String authHeader) {
    try {
      validateAdminAccess(authHeader);
      AdminStatsDto health = adminService.getSystemHealth();
      return ResponseEntity.ok(health);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
    }
  }
}
