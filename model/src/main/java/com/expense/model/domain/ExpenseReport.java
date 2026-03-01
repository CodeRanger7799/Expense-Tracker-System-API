package com.expense.model.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import com.expense.model.enums.Category;

public class ExpenseReport {
    
    private BigDecimal totalAmount;
    private Long expenseCount;
    private Map<Category, BigDecimal> categorySubtotals;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime generatedAt;
    
    public ExpenseReport() {
    }
    
    public ExpenseReport(BigDecimal totalAmount, Long expenseCount, 
                        Map<Category, BigDecimal> categorySubtotals,
                        LocalDate startDate, LocalDate endDate, LocalDateTime generatedAt) {
        this.totalAmount = totalAmount;
        this.expenseCount = expenseCount;
        this.categorySubtotals = categorySubtotals;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedAt = generatedAt;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Long getExpenseCount() {
        return expenseCount;
    }
    
    public void setExpenseCount(Long expenseCount) {
        this.expenseCount = expenseCount;
    }
    
    public Map<Category, BigDecimal> getCategorySubtotals() {
        return categorySubtotals;
    }
    
    public void setCategorySubtotals(Map<Category, BigDecimal> categorySubtotals) {
        this.categorySubtotals = categorySubtotals;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
