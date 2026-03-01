package com.expense.management.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.expense.dbadapter.repository.ExpenseRepository;
import com.expense.model.dto.ExpenseRequest;
import com.expense.model.dto.ExpenseResponse;
import com.expense.model.entity.Expense;
import com.expense.model.enums.Category;
import com.expense.model.enums.Status;
import com.expense.model.exception.ExpenseNotFoundException;
import com.expense.model.exception.ValidationException;

/**
 * Service layer for managing expense operations.
 * Provides business logic for creating, retrieving, updating, and deleting expenses,
 * as well as filtering and validation functionality.
 */
@Service
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    
    /**
     * Constructs an ExpenseService with the required repository dependency.
     *
     * @param expenseRepository the repository for expense data access operations
     */
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }
    
    /**
     * Creates a new expense record in the system.
     * Validates the expense data, sets the status (from request or defaults to PENDING),
     * and persists the expense to the database with creation and update timestamps.
     *
     * @param request the expense creation request containing amount, date, category, description, and optional status
     * @return the created expense as an ExpenseResponse with generated ID and timestamps
     * @throws ValidationException if the expense data fails validation (negative amount, future date, invalid category/status)
     */
    public ExpenseResponse createExpense(ExpenseRequest request) {
        validateExpense(request);

        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate());
        expense.setCategory(parseCategory(request.getCategory()));
        expense.setDescription(request.getDescription());

        // Use status from request if provided, otherwise default to PENDING
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            expense.setStatus(parseStatus(request.getStatus()));
        } else {
            expense.setStatus(Status.PENDING);
        }

        LocalDateTime now = LocalDateTime.now();
        expense.setCreatedAt(now);
        expense.setUpdatedAt(now);

        Expense savedExpense = expenseRepository.save(expense);

        return convertToResponse(savedExpense);
    }
    
    /**
     * Retrieves a specific expense by its unique identifier.
     *
     * @param id the unique identifier of the expense to retrieve
     * @return the expense details as an ExpenseResponse
     * @throws ExpenseNotFoundException if no expense exists with the given ID
     */
    public ExpenseResponse getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
        
        return convertToResponse(expense);
    }
    
    /**
     * Retrieves all expenses in the system, ordered by date in descending order (newest first).
     *
     * @return a list of all expenses as ExpenseResponse objects, or an empty list if no expenses exist
     */
    public List<ExpenseResponse> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAllByOrderByDateDesc();
        
        return expenses.stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    /**
     * Filters expenses based on multiple optional criteria: date range, categories, and status.
     * All filter parameters are optional and can be combined. Results are ordered by date descending.
     *
     * @param startDate the start date for filtering (inclusive), or null to include all dates from the beginning
     * @param endDate the end date for filtering (inclusive), or null to include all dates to the present
     * @param categories a list of category names to filter by, or null/empty to include all categories
     * @param status the status to filter by, or null/blank to include all statuses
     * @return a list of expenses matching the filter criteria as ExpenseResponse objects
     * @throws ValidationException if endDate is before startDate, or if any category/status value is invalid
     */
    public List<ExpenseResponse> filterExpenses(LocalDate startDate, LocalDate endDate, 
                                                 List<String> categories, String status) {
        // Validate date range
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new ValidationException("End date cannot be before start date");
        }

        List<Category> categoryEnums = null;
        if (categories != null && !categories.isEmpty()) {
            categoryEnums = categories.stream()
                    .map(this::parseCategory)
                    .toList();
        }

        Status statusEnum = null;
        if (status != null && !status.isBlank()) {
            statusEnum = parseStatus(status);
        }

        List<Expense> expenses = expenseRepository.findByFilters(startDate, endDate, categoryEnums, statusEnum);

        return expenses.stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    /**
     * Updates an existing expense with new data.
     * Validates the new expense data and updates all modifiable fields.
     * The updatedAt timestamp is automatically set to the current time.
     *
     * @param id the unique identifier of the expense to update
     * @param request the expense update request containing new values for amount, date, category, description, and optional status
     * @return the updated expense as an ExpenseResponse
     * @throws ExpenseNotFoundException if no expense exists with the given ID
     * @throws ValidationException if the expense data fails validation (negative amount, future date, invalid category/status)
     */
    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
        
        validateExpense(request);
        
        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate());
        expense.setCategory(parseCategory(request.getCategory()));
        expense.setDescription(request.getDescription());
        
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            expense.setStatus(parseStatus(request.getStatus()));
        }
        
        expense.setUpdatedAt(LocalDateTime.now());
        
        Expense updatedExpense = expenseRepository.save(expense);
        
        return convertToResponse(updatedExpense);
    }
    
    /**
     * Deletes an expense from the system.
     *
     * @param id the unique identifier of the expense to delete
     * @throws ExpenseNotFoundException if no expense exists with the given ID
     */
    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
        
        expenseRepository.delete(expense);
    }
    
    /**
     * Retrieves all available expense categories in the system.
     *
     * @return a list of category names as strings (e.g., "TRAVEL", "FOOD", "OFFICE_SUPPLIES", "UTILITIES", "ENTERTAINMENT")
     */
    public List<String> getAllCategories() {
        return Arrays.stream(Category.values())
                .map(Category::name)
                .toList();
    }
    
    /**
     * Validates expense request data against business rules.
     * Checks that amount is positive, date is not in the future, and category/status are valid.
     *
     * @param request the expense request to validate
     * @throws ValidationException if validation fails, containing a list of all validation errors
     */
    public void validateExpense(ExpenseRequest request) {
        List<String> errors = new ArrayList<>();
        
        // Validate amount
        if (request.getAmount() != null && request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Amount must be positive");
        }
        
        // Validate date
        if (request.getDate() != null && request.getDate().isAfter(LocalDate.now())) {
            errors.add("Date cannot be in the future");
        }
        
        // Validate category
        if (request.getCategory() != null) {
            try {
                parseCategory(request.getCategory());
            } catch (ValidationException e) {
                errors.add(e.getMessage());
            }
        }
        
        // Validate status if provided
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                parseStatus(request.getStatus());
            } catch (ValidationException e) {
                errors.add(e.getMessage());
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }
    
    /**
     * Parses a category string into a Category enum value.
     * The parsing is case-insensitive.
     *
     * @param category the category string to parse (e.g., "travel", "TRAVEL")
     * @return the corresponding Category enum value
     * @throws ValidationException if the category is null or not a valid category name
     */
    private Category parseCategory(String category) {
        if (category == null) {
            throw new ValidationException("Category cannot be null");
        }
        try {
            return Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid category: " + category);
        }
    }
    
    /**
     * Parses a status string into a Status enum value.
     * The parsing is case-insensitive.
     *
     * @param status the status string to parse (e.g., "pending", "PENDING")
     * @return the corresponding Status enum value
     * @throws ValidationException if the status is null or not a valid status name
     */
    private Status parseStatus(String status) {
        if (status == null) {
            throw new ValidationException("Status cannot be null");
        }
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid status: " + status);
        }
    }
    
    /**
     * Converts an Expense entity to an ExpenseResponse DTO.
     * Maps all entity fields to the response object, converting enums to their string names.
     *
     * @param expense the expense entity to convert
     * @return the expense data as an ExpenseResponse DTO
     */
    private ExpenseResponse convertToResponse(Expense expense) {
        ExpenseResponse response = new ExpenseResponse();
        response.setId(expense.getId());
        response.setAmount(expense.getAmount());
        response.setDate(expense.getDate());
        response.setCategory(expense.getCategory().name());
        response.setDescription(expense.getDescription());
        response.setStatus(expense.getStatus().name());
        response.setCreatedAt(expense.getCreatedAt());
        response.setUpdatedAt(expense.getUpdatedAt());
        
        return response;
    }
}
