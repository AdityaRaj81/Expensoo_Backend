package com.expenso.expense_tracker.controller;

import com.expenso.expense_tracker.dto.AnalyticsDTO;
import com.expenso.expense_tracker.model.Transaction;
import com.expenso.expense_tracker.repository.TransactionRepository;
import com.expenso.expense_tracker.security.JwtService;
import com.expenso.expense_tracker.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

  private final AnalyticsService analyticsService;
  private final JwtService jwtService;
  private final TransactionRepository transactionRepository;

  @GetMapping
  public ResponseEntity<AnalyticsDTO> getAnalytics(
      @RequestHeader("Authorization") String token,
      @RequestParam(defaultValue = "12") int months) {

    UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
    AnalyticsDTO analytics = analyticsService.getAnalytics(userId, months);
    return ResponseEntity.ok(analytics);
  }

  @GetMapping("/export/csv")
  public ResponseEntity<String> exportToCsv(
      @RequestHeader("Authorization") String token,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

    UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));

    if (startDate == null) {
      startDate = LocalDate.now().minusYears(1);
    }
    if (endDate == null) {
      endDate = LocalDate.now();
    }

    List<Transaction> transactions = transactionRepository
        .findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

    StringBuilder csv = new StringBuilder();
    csv.append("Date,Type,Category,Amount,Notes\n");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    for (Transaction t : transactions) {
      csv.append(t.getDate().format(formatter)).append(",")
          .append(t.getType()).append(",")
          .append(t.getCategory()).append(",")
          .append(t.getAmount()).append(",")
          .append("\"").append(t.getNotes() != null ? t.getNotes().replace("\"", "\"\"") : "").append("\"")
          .append("\n");
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType("text/csv"));
    headers.setContentDispositionFormData("attachment", "transactions_" +
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv");

    return new ResponseEntity<>(csv.toString(), headers, HttpStatus.OK);
  }

  @GetMapping("/export/excel")
  public ResponseEntity<String> exportToExcel(
      @RequestHeader("Authorization") String token,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

    // Return CSV format that Excel can open (same as CSV but with different
    // content-type)
    UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));

    if (startDate == null) {
      startDate = LocalDate.now().minusYears(1);
    }
    if (endDate == null) {
      endDate = LocalDate.now();
    }

    List<Transaction> transactions = transactionRepository
        .findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

    StringBuilder csv = new StringBuilder();
    csv.append("Date,Type,Category,Amount,Notes\n");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    for (Transaction t : transactions) {
      csv.append(t.getDate().format(formatter)).append(",")
          .append(t.getType()).append(",")
          .append(t.getCategory()).append(",")
          .append(t.getAmount()).append(",")
          .append("\"").append(t.getNotes() != null ? t.getNotes().replace("\"", "\"\"") : "").append("\"")
          .append("\n");
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
    headers.setContentDispositionFormData("attachment", "transactions_" +
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv");

    return new ResponseEntity<>(csv.toString(), headers, HttpStatus.OK);
  }
}
