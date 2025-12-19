package com.expenso.expense_tracker.service;

import com.expenso.expense_tracker.dto.AnalyticsDTO;
import com.expenso.expense_tracker.model.Transaction;
import com.expenso.expense_tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

  private final TransactionRepository transactionRepository;

  public AnalyticsDTO getAnalytics(UUID userId, int months) {
    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.minusMonths(months);

    List<Transaction> transactions = transactionRepository
        .findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

    AnalyticsDTO analytics = new AnalyticsDTO();
    analytics.setTrendData(calculateTrendData(transactions, months));
    analytics.setComparisonData(calculateComparisonData(transactions));
    analytics.setCategoryTrends(calculateCategoryTrends(transactions));
    analytics.setMonthlyComparison(calculateMonthlyComparison(transactions, months));
    analytics.setForecastData(calculateForecast(transactions));

    return analytics;
  }

  private AnalyticsDTO.TrendData calculateTrendData(List<Transaction> transactions, int months) {
    LocalDate endDate = LocalDate.now();
    Map<YearMonth, Double> incomeByMonth = new TreeMap<>();
    Map<YearMonth, Double> expenseByMonth = new TreeMap<>();

    // Initialize all months with 0
    for (int i = months - 1; i >= 0; i--) {
      YearMonth month = YearMonth.from(endDate.minusMonths(i));
      incomeByMonth.put(month, 0.0);
      expenseByMonth.put(month, 0.0);
    }

    // Aggregate transactions by month
    for (Transaction t : transactions) {
      YearMonth month = YearMonth.from(t.getDate());
      if (t.getType().equalsIgnoreCase("income")) {
        incomeByMonth.merge(month, t.getAmount(), Double::sum);
      } else {
        expenseByMonth.merge(month, t.getAmount(), Double::sum);
      }
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

    List<AnalyticsDTO.DataPoint> incomeTrend = incomeByMonth.entrySet().stream()
        .map(e -> new AnalyticsDTO.DataPoint(
            e.getKey().format(formatter),
            e.getValue()))
        .collect(Collectors.toList());

    List<AnalyticsDTO.DataPoint> expenseTrend = expenseByMonth.entrySet().stream()
        .map(e -> new AnalyticsDTO.DataPoint(
            e.getKey().format(formatter),
            e.getValue()))
        .collect(Collectors.toList());

    List<AnalyticsDTO.DataPoint> balanceTrend = new ArrayList<>();
    for (int i = 0; i < incomeTrend.size(); i++) {
      balanceTrend.add(new AnalyticsDTO.DataPoint(
          incomeTrend.get(i).getDate(),
          incomeTrend.get(i).getValue() - expenseTrend.get(i).getValue()));
    }

    double avgIncome = incomeByMonth.values().stream()
        .mapToDouble(Double::doubleValue).average().orElse(0.0);
    double avgExpense = expenseByMonth.values().stream()
        .mapToDouble(Double::doubleValue).average().orElse(0.0);
    double savingsRate = avgIncome > 0 ? ((avgIncome - avgExpense) / avgIncome) * 100 : 0;

    return new AnalyticsDTO.TrendData(
        incomeTrend, expenseTrend, balanceTrend,
        avgIncome, avgExpense, savingsRate);
  }

  private AnalyticsDTO.ComparisonData calculateComparisonData(List<Transaction> transactions) {
    YearMonth currentMonth = YearMonth.now();
    YearMonth previousMonth = currentMonth.minusMonths(1);

    double currentIncome = transactions.stream()
        .filter(t -> YearMonth.from(t.getDate()).equals(currentMonth))
        .filter(t -> t.getType().equalsIgnoreCase("income"))
        .mapToDouble(Transaction::getAmount)
        .sum();

    double previousIncome = transactions.stream()
        .filter(t -> YearMonth.from(t.getDate()).equals(previousMonth))
        .filter(t -> t.getType().equalsIgnoreCase("income"))
        .mapToDouble(Transaction::getAmount)
        .sum();

    double currentExpense = transactions.stream()
        .filter(t -> YearMonth.from(t.getDate()).equals(currentMonth))
        .filter(t -> t.getType().equalsIgnoreCase("expense"))
        .mapToDouble(Transaction::getAmount)
        .sum();

    double previousExpense = transactions.stream()
        .filter(t -> YearMonth.from(t.getDate()).equals(previousMonth))
        .filter(t -> t.getType().equalsIgnoreCase("expense"))
        .mapToDouble(Transaction::getAmount)
        .sum();

    double incomeChange = currentIncome - previousIncome;
    double incomeChangePercent = previousIncome > 0 ? (incomeChange / previousIncome) * 100 : 0;

    double expenseChange = currentExpense - previousExpense;
    double expenseChangePercent = previousExpense > 0 ? (expenseChange / previousExpense) * 100 : 0;

    return new AnalyticsDTO.ComparisonData(
        currentIncome, previousIncome, incomeChange, incomeChangePercent,
        currentExpense, previousExpense, expenseChange, expenseChangePercent);
  }

  private List<AnalyticsDTO.CategoryTrendDTO> calculateCategoryTrends(List<Transaction> transactions) {
    Map<String, List<Transaction>> byCategory = transactions.stream()
        .filter(t -> t.getType().equalsIgnoreCase("expense"))
        .collect(Collectors.groupingBy(Transaction::getCategory));

    double totalExpenses = transactions.stream()
        .filter(t -> t.getType().equalsIgnoreCase("expense"))
        .mapToDouble(Transaction::getAmount)
        .sum();

    List<AnalyticsDTO.CategoryTrendDTO> categoryTrends = new ArrayList<>();

    for (Map.Entry<String, List<Transaction>> entry : byCategory.entrySet()) {
      String category = entry.getKey();
      List<Transaction> categoryTransactions = entry.getValue();

      Map<YearMonth, Double> monthlyData = new TreeMap<>();
      for (Transaction t : categoryTransactions) {
        YearMonth month = YearMonth.from(t.getDate());
        monthlyData.merge(month, t.getAmount(), Double::sum);
      }

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
      List<AnalyticsDTO.DataPoint> trend = monthlyData.entrySet().stream()
          .map(e -> new AnalyticsDTO.DataPoint(
              e.getKey().format(formatter),
              e.getValue()))
          .collect(Collectors.toList());

      double total = categoryTransactions.stream()
          .mapToDouble(Transaction::getAmount)
          .sum();
      double average = total / Math.max(monthlyData.size(), 1);
      double percentage = totalExpenses > 0 ? (total / totalExpenses) * 100 : 0;

      categoryTrends.add(new AnalyticsDTO.CategoryTrendDTO(
          category, trend, total, average, percentage));
    }

    return categoryTrends.stream()
        .sorted((a, b) -> Double.compare(b.getTotal(), a.getTotal()))
        .collect(Collectors.toList());
  }

  private List<AnalyticsDTO.MonthlyComparisonDTO> calculateMonthlyComparison(
      List<Transaction> transactions, int months) {

    Map<YearMonth, List<Transaction>> byMonth = transactions.stream()
        .collect(Collectors.groupingBy(t -> YearMonth.from(t.getDate())));

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

    return byMonth.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .map(entry -> {
          YearMonth month = entry.getKey();
          List<Transaction> monthTransactions = entry.getValue();

          double income = monthTransactions.stream()
              .filter(t -> t.getType().equalsIgnoreCase("income"))
              .mapToDouble(Transaction::getAmount)
              .sum();

          double expense = monthTransactions.stream()
              .filter(t -> t.getType().equalsIgnoreCase("expense"))
              .mapToDouble(Transaction::getAmount)
              .sum();

          double balance = income - expense;
          double savingsRate = income > 0 ? (balance / income) * 100 : 0;

          return new AnalyticsDTO.MonthlyComparisonDTO(
              month.format(formatter), income, expense, balance, savingsRate);
        })
        .collect(Collectors.toList());
  }

  private AnalyticsDTO.ForecastData calculateForecast(List<Transaction> transactions) {
    // Simple moving average forecast for next 3 months
    Map<YearMonth, Double> incomeByMonth = new TreeMap<>();
    Map<YearMonth, Double> expenseByMonth = new TreeMap<>();

    for (Transaction t : transactions) {
      YearMonth month = YearMonth.from(t.getDate());
      if (t.getType().equalsIgnoreCase("income")) {
        incomeByMonth.merge(month, t.getAmount(), Double::sum);
      } else {
        expenseByMonth.merge(month, t.getAmount(), Double::sum);
      }
    }

    double avgIncome = incomeByMonth.values().stream()
        .mapToDouble(Double::doubleValue).average().orElse(0.0);
    double avgExpense = expenseByMonth.values().stream()
        .mapToDouble(Double::doubleValue).average().orElse(0.0);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
    List<AnalyticsDTO.DataPoint> forecastedIncome = new ArrayList<>();
    List<AnalyticsDTO.DataPoint> forecastedExpense = new ArrayList<>();

    YearMonth currentMonth = YearMonth.now();
    for (int i = 1; i <= 3; i++) {
      YearMonth futureMonth = currentMonth.plusMonths(i);
      forecastedIncome.add(new AnalyticsDTO.DataPoint(
          futureMonth.format(formatter), avgIncome));
      forecastedExpense.add(new AnalyticsDTO.DataPoint(
          futureMonth.format(formatter), avgExpense));
    }

    return new AnalyticsDTO.ForecastData(
        forecastedIncome, forecastedExpense,
        "Simple Moving Average (3-month forecast)");
  }
}
