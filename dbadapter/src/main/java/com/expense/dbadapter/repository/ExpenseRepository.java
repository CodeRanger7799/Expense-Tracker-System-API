package com.expense.dbadapter.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.expense.model.entity.Expense;
import com.expense.model.enums.Category;
import com.expense.model.enums.Status;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    List<Expense> findAllByOrderByDateDesc();
    
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Expense> findByCategoryIn(List<Category> categories);
    
    List<Expense> findByStatus(Status status);
    
    @Query("SELECT e FROM Expense e WHERE " +
           "(:startDate IS NULL OR e.date >= :startDate) AND " +
           "(:endDate IS NULL OR e.date <= :endDate) AND " +
           "(:categories IS NULL OR e.category IN :categories) AND " +
           "(:status IS NULL OR e.status = :status) " +
           "ORDER BY e.date DESC")
    List<Expense> findByFilters(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("categories") List<Category> categories,
        @Param("status") Status status
    );
}
