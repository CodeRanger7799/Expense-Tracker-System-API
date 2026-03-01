package com.expense.facade.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.expense.api.ReportsApi;
import com.expense.api.model.ExpenseReportApiResponse;
import com.expense.facade.mapper.ReportMapper;
import com.expense.management.service.ReportService;
import com.expense.model.dto.ExpenseReportResponse;

@RestController
public class ReportController implements ReportsApi {
    
    private final ReportService reportService;
    private final ReportMapper reportMapper;
    
    public ReportController(ReportService reportService, ReportMapper reportMapper) {
        this.reportService = reportService;
        this.reportMapper = reportMapper;
    }
    
    @Override
    public ResponseEntity<ExpenseReportApiResponse> generateReport(
            LocalDate startDate, 
            LocalDate endDate, 
            List<String> categories, 
            String status) {
        
        // Call service with query parameters
        ExpenseReportResponse response = reportService.generateReport(
                startDate, endDate, categories, status);
        
        // Convert internal DTO to API model
        ExpenseReportApiResponse apiResponse = reportMapper.dtoToApiResponse(response);
        
        // Return 200 OK
        return ResponseEntity.ok(apiResponse);
    }
}
