package com.expense.facade.controller;

import com.expense.api.CategoriesApi;
import com.expense.management.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController implements CategoriesApi {
    
    private final ExpenseService expenseService;
    
    public CategoryController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }
    
    @Override
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = expenseService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}
