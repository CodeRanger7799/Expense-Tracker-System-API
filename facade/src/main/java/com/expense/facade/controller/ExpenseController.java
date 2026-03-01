package com.expense.facade.controller;

import com.expense.api.ExpensesApi;
import com.expense.api.model.ExpenseApiRequest;
import com.expense.api.model.ExpenseApiResponse;
import com.expense.facade.mapper.ExpenseMapper;
import com.expense.management.service.ExpenseService;
import com.expense.model.dto.ExpenseRequest;
import com.expense.model.dto.ExpenseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ExpenseController implements ExpensesApi {
    
    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;
    
    public ExpenseController(ExpenseService expenseService, ExpenseMapper expenseMapper) {
        this.expenseService = expenseService;
        this.expenseMapper = expenseMapper;
    }
    
    @Override
    public ResponseEntity<ExpenseApiResponse> createExpense(ExpenseApiRequest expenseApiRequest) {
        // Convert API model to internal DTO
        ExpenseRequest dto = expenseMapper.apiRequestToDto(expenseApiRequest);
        
        // Call service
        ExpenseResponse response = expenseService.createExpense(dto);
        
        // Convert internal DTO to API model
        ExpenseApiResponse apiResponse = expenseMapper.dtoToApiResponse(response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    
    @Override
    public ResponseEntity<ExpenseApiResponse> getExpenseById(Long id) {
        ExpenseResponse response = expenseService.getExpenseById(id);
        ExpenseApiResponse apiResponse = expenseMapper.dtoToApiResponse(response);
        return ResponseEntity.ok(apiResponse);
    }
    
    @Override
    public ResponseEntity<List<ExpenseApiResponse>> getAllExpenses(
            LocalDate startDate, LocalDate endDate, List<String> categories, String status) {
        
        List<ExpenseResponse> responses = expenseService.filterExpenses(
                startDate, endDate, categories, status);
        List<ExpenseApiResponse> apiResponses = expenseMapper.dtoListToApiResponseList(responses);
        return ResponseEntity.ok(apiResponses);
    }
    
    @Override
    public ResponseEntity<ExpenseApiResponse> updateExpense(Long id, ExpenseApiRequest expenseApiRequest) {
        ExpenseRequest dto = expenseMapper.apiRequestToDto(expenseApiRequest);
        ExpenseResponse response = expenseService.updateExpense(id, dto);
        ExpenseApiResponse apiResponse = expenseMapper.dtoToApiResponse(response);
        return ResponseEntity.ok(apiResponse);
    }
    
    @Override
    public ResponseEntity<Void> deleteExpense(Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
