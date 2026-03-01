package com.expense.management.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.expense.model.dto.ExpenseReportResponse;
import com.expense.model.dto.ExpenseResponse;

@Service
public class ReportService {
    
    private final ExpenseService expenseService;
    
    public ReportService(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }
    
    public ExpenseReportResponse generateReport(LocalDate startDate, LocalDate endDate, 
                                                List<String> categories, String status) {
        // Call filterExpenses with parameters
        List<ExpenseResponse> expenses = expenseService.filterExpenses(startDate, endDate, categories, status);
        
        // Calculate totalAmount (sum)
        BigDecimal totalAmount = expenses.stream()
                .map(ExpenseResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate expenseCount
        Long expenseCount = (long) expenses.size();
        
        // Calculate categorySubtotals (group by category)
        Map<String, BigDecimal> categorySubtotals = calculateCategorySubtotals(expenses);
        
        // Set date range and generatedAt
        LocalDateTime generatedAt = LocalDateTime.now();
        
        // Return ExpenseReportResponse
        ExpenseReportResponse response = new ExpenseReportResponse();
        response.setTotalAmount(totalAmount);
        response.setExpenseCount(expenseCount);
        response.setCategorySubtotals(categorySubtotals);
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setGeneratedAt(generatedAt);
        
        return response;
    }
    
    public Map<String, BigDecimal> calculateCategorySubtotals(List<ExpenseResponse> expenses) {
        Map<String, BigDecimal> categorySubtotals = new HashMap<>();
        
        // Group expenses by category and sum amounts per category
        for (ExpenseResponse expense : expenses) {
            String category = expense.getCategory();
            BigDecimal amount = expense.getAmount();
            
            categorySubtotals.merge(category, amount, BigDecimal::add);
        }
        
        return categorySubtotals;
    }
}
