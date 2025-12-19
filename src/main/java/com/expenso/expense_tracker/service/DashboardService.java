package com.expenso.expense_tracker.service;

import com.expenso.expense_tracker.dto.*;
import com.expenso.expense_tracker.model.Transaction;
import com.expenso.expense_tracker.model.User;
import com.expenso.expense_tracker.repository.UserRepository;
import com.expenso.expense_tracker.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

        private final TransactionRepository transactionRepository;
        private final UserRepository userRepository;

        public DashboardResponse getDashboardData(UUID userId) {
                // ✅ Step 1: Fetch user from DB using userId
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

                List<Transaction> transactions = transactionRepository.findByUserId(userId);

                double totalIncome = transactions.stream()
                                .filter(t -> t.getType().equalsIgnoreCase("income"))
                                .mapToDouble(Transaction::getAmount)
                                .sum();

                double totalExpenses = transactions.stream()
                                .filter(t -> t.getType().equalsIgnoreCase("expense"))
                                .mapToDouble(Transaction::getAmount)
                                .sum();

                double balance = totalIncome - totalExpenses;

                List<TransactionDTO> recentTransactions = transactions.stream()
                                .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
                                .limit(5)
                                .map(t -> new TransactionDTO(
                                                t.getId().toString(),
                                                t.getAmount(),
                                                t.getCategory(),
                                                t.getType(),
                                                t.getDate(),
                                                t.getNotes()))
                                .collect(Collectors.toList());

                // ✅ Generate chart data
                List<MonthlyDataDTO> monthlyData = generateMonthlyData(transactions);
                List<CategoryDataDTO> categoryData = generateCategoryData(transactions);

                return new DashboardResponse(
                                totalIncome,
                                totalExpenses,
                                balance,
                                recentTransactions,
                                monthlyData,
                                categoryData);
        }

        // ✅ Line Chart: Income vs Expenses per month (last 6 months)
        private List<MonthlyDataDTO> generateMonthlyData(List<Transaction> transactions) {
                LocalDate now = LocalDate.now();
                List<MonthlyDataDTO> result = new ArrayList<>();

                for (int i = 5; i >= 0; i--) {
                        LocalDate monthDate = now.minusMonths(i);
                        String monthName = monthDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

                        double income = transactions.stream()
                                        .filter(t -> t.getType().equalsIgnoreCase("income") &&
                                                        t.getDate().getMonthValue() == monthDate.getMonthValue() &&
                                                        t.getDate().getYear() == monthDate.getYear())
                                        .mapToDouble(Transaction::getAmount)
                                        .sum();

                        double expense = transactions.stream()
                                        .filter(t -> t.getType().equalsIgnoreCase("expense") &&
                                                        t.getDate().getMonthValue() == monthDate.getMonthValue() &&
                                                        t.getDate().getYear() == monthDate.getYear())
                                        .mapToDouble(Transaction::getAmount)
                                        .sum();

                        result.add(new MonthlyDataDTO(monthName, income, expense, income - expense));
                }

                return result;
        }

        // ✅ Pie Chart: Expense breakdown by category (this month)
        private List<CategoryDataDTO> generateCategoryData(List<Transaction> transactions) {
                LocalDate now = LocalDate.now();
                int thisMonth = now.getMonthValue();
                int thisYear = now.getYear();

                return transactions.stream()
                                .filter(t -> t.getType().equalsIgnoreCase("expense") &&
                                                t.getDate().getMonthValue() == thisMonth &&
                                                t.getDate().getYear() == thisYear)
                                .collect(Collectors.groupingBy(Transaction::getCategory,
                                                Collectors.summingDouble(Transaction::getAmount)))
                                .entrySet().stream()
                                .map(entry -> new CategoryDataDTO(entry.getKey(), entry.getValue()))
                                .collect(Collectors.toList());
        }
}
