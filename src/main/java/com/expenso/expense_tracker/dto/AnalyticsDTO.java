package com.expenso.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDTO {
  private TrendData trendData;
  private ComparisonData comparisonData;
  private List<CategoryTrendDTO> categoryTrends;
  private List<MonthlyComparisonDTO> monthlyComparison;
  private ForecastData forecastData;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TrendData {
    private List<DataPoint> incometrend;
    private List<DataPoint> expenseTrend;
    private List<DataPoint> balanceTrend;
    private double averageMonthlyIncome;
    private double averageMonthlyExpense;
    private double savingsRate;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DataPoint {
    private String date;
    private double value;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ComparisonData {
    private double currentMonthIncome;
    private double previousMonthIncome;
    private double incomeChange;
    private double incomeChangePercent;
    private double currentMonthExpense;
    private double previousMonthExpense;
    private double expenseChange;
    private double expenseChangePercent;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CategoryTrendDTO {
    private String category;
    private List<DataPoint> trend;
    private double total;
    private double average;
    private double percentageOfTotal;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MonthlyComparisonDTO {
    private String month;
    private double income;
    private double expense;
    private double balance;
    private double savingsRate;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ForecastData {
    private List<DataPoint> forecastedIncome;
    private List<DataPoint> forecastedExpense;
    private String methodology;
  }
}
