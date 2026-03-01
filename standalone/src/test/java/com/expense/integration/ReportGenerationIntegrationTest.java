package com.expense.integration;

import com.expense.api.model.ExpenseApiRequest;
import com.expense.api.model.ExpenseApiResponse;
import com.expense.api.model.ExpenseReportApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Integration tests for expense report generation.
 * Tests Requirements: 9.1-9.5, 12.7
 */
class ReportGenerationIntegrationTest extends BaseIntegrationTest {

    @BeforeEach
    void setupTestData() {
        // Create test expenses for report generation
        createExpense(100.00, LocalDate.of(2024, 1, 15), "TRAVEL", "APPROVED");
        createExpense(50.50, LocalDate.of(2024, 1, 20), "FOOD", "APPROVED");
        createExpense(200.00, LocalDate.of(2024, 2, 10), "TRAVEL", "APPROVED");
        createExpense(75.25, LocalDate.of(2024, 2, 25), "OFFICE_SUPPLIES", "REJECTED");
        createExpense(150.00, LocalDate.of(2024, 3, 5), "ENTERTAINMENT", "PENDING");
        createExpense(300.00, LocalDate.of(2024, 3, 15), "UTILITIES", "APPROVED");
    }

    @Test
    void testGenerateReportWithAllExpenses() {
        // Test report generation without filters (Requirement 9.1, 9.2, 9.3, 9.5)
        ResponseEntity<ExpenseReportApiResponse> response = restTemplate.exchange(
            url("/api/reports"),
            HttpMethod.GET,
            null,
            ExpenseReportApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        ExpenseReportApiResponse report = response.getBody();

        // Verify total amount calculation (Requirement 9.1)
        assertThat(report.getTotalAmount()).isCloseTo(875.75, within(0.01));

        // Verify expense count (Requirement 9.2)
        assertThat(report.getExpenseCount()).isEqualTo(6);

        // Verify category subtotals (Requirement 9.3)
        Map<String, Double> subtotals = report.getCategorySubtotals();
        assertThat(subtotals).isNotNull();
        assertThat(subtotals.get("TRAVEL")).isCloseTo(300.00, within(0.01));
        assertThat(subtotals.get("FOOD")).isCloseTo(50.50, within(0.01));
        assertThat(subtotals.get("OFFICE_SUPPLIES")).isCloseTo(75.25, within(0.01));
        assertThat(subtotals.get("ENTERTAINMENT")).isCloseTo(150.00, within(0.01));
        assertThat(subtotals.get("UTILITIES")).isCloseTo(300.00, within(0.01));

        // Verify generatedAt timestamp is present
        assertThat(report.getGeneratedAt()).isNotNull();
    }

    @Test
    void testGenerateReportWithDateRangeFilter() {
        // Test report with date range filter (Requirement 9.4)
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/reports"))
            .queryParam("startDate", "2024-02-01")
            .queryParam("endDate", "2024-02-28")
            .toUriString();

        ResponseEntity<ExpenseReportApiResponse> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            ExpenseReportApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        ExpenseReportApiResponse report = response.getBody();

        // Verify filtered total (Requirement 9.1, 9.4)
        assertThat(report.getTotalAmount()).isCloseTo(275.25, within(0.01));

        // Verify filtered count (Requirement 9.2, 9.4)
        assertThat(report.getExpenseCount()).isEqualTo(2);

        // Verify date range in report
        assertThat(report.getStartDate()).isEqualTo(LocalDate.of(2024, 2, 1));
        assertThat(report.getEndDate()).isEqualTo(LocalDate.of(2024, 2, 28));

        // Verify category subtotals for filtered data (Requirement 9.3, 9.4)
        Map<String, Double> subtotals = report.getCategorySubtotals();
        assertThat(subtotals).hasSize(2);
        assertThat(subtotals.get("TRAVEL")).isCloseTo(200.00, within(0.01));
        assertThat(subtotals.get("OFFICE_SUPPLIES")).isCloseTo(75.25, within(0.01));
    }

    @Test
    void testGenerateReportWithCategoryFilter() {
        // Test report with category filter (Requirement 9.4)
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/reports"))
            .queryParam("categories", "TRAVEL")
            .toUriString();

        ResponseEntity<ExpenseReportApiResponse> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            ExpenseReportApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        ExpenseReportApiResponse report = response.getBody();

        // Verify filtered total
        assertThat(report.getTotalAmount()).isCloseTo(300.00, within(0.01));

        // Verify filtered count
        assertThat(report.getExpenseCount()).isEqualTo(2);

        // Verify only TRAVEL category in subtotals
        Map<String, Double> subtotals = report.getCategorySubtotals();
        assertThat(subtotals).hasSize(1);
        assertThat(subtotals).containsKey("TRAVEL");
    }

    @Test
    void testGenerateReportWithStatusFilter() {
        // Test report with status filter (Requirement 9.4)
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/reports"))
            .queryParam("status", "APPROVED")
            .toUriString();

        ResponseEntity<ExpenseReportApiResponse> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            ExpenseReportApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        ExpenseReportApiResponse report = response.getBody();

        // Verify filtered total (only APPROVED expenses)
        assertThat(report.getTotalAmount()).isCloseTo(650.50, within(0.01));

        // Verify filtered count
        assertThat(report.getExpenseCount()).isEqualTo(4);

        // Verify category subtotals for APPROVED expenses only
        Map<String, Double> subtotals = report.getCategorySubtotals();
        assertThat(subtotals).hasSize(3);
        assertThat(subtotals).containsKeys("TRAVEL", "FOOD", "UTILITIES");
        assertThat(subtotals).doesNotContainKey("OFFICE_SUPPLIES"); // REJECTED
        assertThat(subtotals).doesNotContainKey("ENTERTAINMENT"); // PENDING
    }

    @Test
    void testGenerateReportWithMultipleFilters() {
        // Test report with multiple filters combined (Requirement 9.4)
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/reports"))
            .queryParam("startDate", "2024-01-01")
            .queryParam("endDate", "2024-02-28")
            .queryParam("categories", "TRAVEL", "FOOD")
            .queryParam("status", "APPROVED")
            .toUriString();

        ResponseEntity<ExpenseReportApiResponse> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            ExpenseReportApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        ExpenseReportApiResponse report = response.getBody();

        // Verify filtered total
        assertThat(report.getTotalAmount()).isCloseTo(350.50, within(0.01));

        // Verify filtered count
        assertThat(report.getExpenseCount()).isEqualTo(3);

        // Verify category subtotals
        Map<String, Double> subtotals = report.getCategorySubtotals();
        assertThat(subtotals).hasSize(2);
        assertThat(subtotals).containsKeys("TRAVEL", "FOOD");
    }

    @Test
    void testGenerateReportWithNoMatchingExpenses() {
        // Test report when no expenses match filters
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/reports"))
            .queryParam("categories", "UTILITIES")
            .queryParam("status", "REJECTED")
            .toUriString();

        ResponseEntity<ExpenseReportApiResponse> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            ExpenseReportApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        ExpenseReportApiResponse report = response.getBody();

        // Verify empty report
        assertThat(report.getTotalAmount()).isCloseTo(0.0, within(0.01));
        assertThat(report.getExpenseCount()).isEqualTo(0);
        assertThat(report.getCategorySubtotals()).isEmpty();
    }

    @Test
    void testGenerateReportWithStartDateOnly() {
        // Test report with only start date filter
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/reports"))
            .queryParam("startDate", "2024-03-01")
            .toUriString();

        ResponseEntity<ExpenseReportApiResponse> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            ExpenseReportApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        ExpenseReportApiResponse report = response.getBody();

        // Verify filtered total (March expenses only)
        assertThat(report.getTotalAmount()).isCloseTo(450.00, within(0.01));

        // Verify filtered count
        assertThat(report.getExpenseCount()).isEqualTo(2);

        // Verify start date in report
        assertThat(report.getStartDate()).isEqualTo(LocalDate.of(2024, 3, 1));
    }

    @Test
    void testGenerateReportWithEndDateOnly() {
        // Test report with only end date filter
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/reports"))
            .queryParam("endDate", "2024-01-31")
            .toUriString();

        ResponseEntity<ExpenseReportApiResponse> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            ExpenseReportApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        ExpenseReportApiResponse report = response.getBody();

        // Verify filtered total (January expenses only)
        assertThat(report.getTotalAmount()).isCloseTo(150.50, within(0.01));

        // Verify filtered count
        assertThat(report.getExpenseCount()).isEqualTo(2);

        // Verify end date in report
        assertThat(report.getEndDate()).isEqualTo(LocalDate.of(2024, 1, 31));
    }

    @Test
    void testReportResponseFormat() {
        // Test that report response matches OpenAPI specification (Requirement 12.7, 12.8)
        ResponseEntity<ExpenseReportApiResponse> response = restTemplate.exchange(
            url("/api/reports"),
            HttpMethod.GET,
            null,
            ExpenseReportApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        ExpenseReportApiResponse report = response.getBody();

        // Verify all required fields are present
        assertThat(report.getTotalAmount()).isNotNull();
        assertThat(report.getExpenseCount()).isNotNull();
        assertThat(report.getCategorySubtotals()).isNotNull();
        assertThat(report.getGeneratedAt()).isNotNull();

        // Verify content type is JSON
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().toString()).contains("application/json");
    }

    // Helper method to create expenses
    private void createExpense(Double amount, LocalDate date, String category, String status) {
        ExpenseApiRequest request = new ExpenseApiRequest();
        request.setAmount(amount);
        request.setDate(date);
        request.setCategory(category);
        request.setDescription("Test expense - " + category);
        request.setStatus(status);

        restTemplate.postForEntity(
            url("/api/expenses"),
            request,
            ExpenseApiResponse.class
        );
    }
}
