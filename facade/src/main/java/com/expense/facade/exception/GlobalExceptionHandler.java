package com.expense.facade.exception;

import com.expense.api.model.ErrorApiResponse;
import com.expense.model.exception.ExpenseNotFoundException;
import com.expense.model.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpenseNotFoundException.class)
    public ResponseEntity<ErrorApiResponse> handleExpenseNotFoundException(ExpenseNotFoundException ex) {
        ErrorApiResponse errorResponse = new ErrorApiResponse()
                .errorCode("NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(OffsetDateTime.now());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorApiResponse> handleValidationException(ValidationException ex) {
        ErrorApiResponse errorResponse = new ErrorApiResponse()
                .errorCode("VALIDATION_ERROR")
                .message(ex.getMessage())
                .details(ex.getErrors())
                .timestamp(OffsetDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.add(error.getField() + ": " + error.getDefaultMessage())
        );
        
        ErrorApiResponse errorResponse = new ErrorApiResponse()
                .errorCode("VALIDATION_ERROR")
                .message("Validation failed")
                .details(errors)
                .timestamp(OffsetDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorApiResponse errorResponse = new ErrorApiResponse()
                .errorCode("INVALID_JSON")
                .message("Invalid JSON format in request body")
                .details(List.of(ex.getMostSpecificCause().getMessage()))
                .timestamp(OffsetDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
