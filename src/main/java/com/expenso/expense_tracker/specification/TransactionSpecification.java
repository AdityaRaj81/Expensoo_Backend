package com.expenso.expense_tracker.specification;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.expenso.expense_tracker.enums.TransactionType;
import com.expenso.expense_tracker.model.Transaction;

public final class TransactionSpecification {

  private TransactionSpecification() {
  }

  public static Specification<Transaction> belongsToUser(UUID userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
        root.get("userId"),
        userId);
  }

  public static Specification<Transaction> hasType(TransactionType type) {
    return (root, query, criteriaBuilder) -> {
      if (type == null) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.equal(
          root.get("type"),
          type);
    };
  }

  public static Specification<Transaction> hasCategory(String category) {
    return (root, query, criteriaBuilder) -> {
      if (category == null || category.isBlank()) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.equal(
          criteriaBuilder.lower(
              root.get("category")),
          category.strip().toLowerCase());
    };
  }

  public static Specification<Transaction> fromDate(LocalDate fromDate) {
    return (root, query, criteriaBuilder) -> {
      if (fromDate == null) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.greaterThanOrEqualTo(
          root.get("date"),
          fromDate);
    };
  }

  public static Specification<Transaction> toDate(LocalDate toDate) {
    return (root, query, criteriaBuilder) -> {
      if (toDate == null) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.lessThanOrEqualTo(
          root.get("date"),
          toDate);
    };
  }

  public static Specification<Transaction> containsSearch(String search) {
    return (root, query, criteriaBuilder) -> {
      if (search == null || search.isBlank()) {
        return criteriaBuilder.conjunction();
      }

      String pattern = "%" +
          search.strip().toLowerCase() +
          "%";

      return criteriaBuilder.or(
          criteriaBuilder.like(
              criteriaBuilder.lower(
                  root.get("category")),
              pattern),
          criteriaBuilder.like(
              criteriaBuilder.lower(
                  root.get("notes")),
              pattern));
    };
  }
}