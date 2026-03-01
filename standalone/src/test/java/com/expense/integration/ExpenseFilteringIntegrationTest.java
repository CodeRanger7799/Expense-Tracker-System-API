package com.expense.integration;

import com.expense.api.model.ExpenseApiRequest;
import com.expense.api.model.ExpenseApiResponse;
import com.expense.api.model.ErrorApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for expense filtering functionality.
 * Tests Requirements: 5.1-5.4, 6.1-6.4, 7.1-7.3, 8.1-8.3, 12.3
 */
class ExpenseFilteringIntegrationTest extends BaseIntegrationTest {

    @BeforeEach
    void setupTestData() {
        // Create test expenses with various attributes
        createExpense(100.0, LocalDate.of(2024, 1, 15), "TRAVEL", "PENDING");
        createExpense(50.0, LocalDate.of(2024, 1, 20), "FOOD", "APPROVED");
        createExpense(200.0, LocalDate.of(2024, 2, 10), "TRAVEL", "APPROVED");
        createExpense(75.0, LocalDate.of(2024, 2, 25), "OFFICE_SUPPLIES", "REJECTED");
        createExpense(150.0, LocalDate.of(2024, 3, 5), "ENTERTAINMENT", "PENDING");
        createExpense(300.0, LocalDate.of(2024, 3, 15), "UTILITIES", "APPROVED");
    }

    @Test
    void testFilterByDateRange_BothDates() {
        // Test filtering with start and end date (Requirement 6.1)
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/expenses"))
            .queryParam("startDate", "2024-02-01")
            .queryParam("endDate", "2024-02-28")
            .toUriString();

        ResponseEntity<List<ExpenseApiResponse>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ExpenseApiResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        
        // Verify all expenses are within date range
        for (ExpenseApiResponse expense : response.getBody()) {
            assertThat(expense.getDate()).isAfterOrEqualTo(LocalDate.of(2024, 2, 1));
            assertThat(expense.getDate()).isBeforeOrEqualTo(LocalDate.of(2024, 2, 28));
        }
    }

    @Test
    void testFilterByDateRange_StartDateOnly() {
        // Test filtering with only start date (Requirement 6.2)
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/expenses"))
            .queryParam("startDate", "2024-03-01")
            .toUriString();

        ResponseEntity<List<ExpenseApiResponse>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ExpenseApiResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        
        // Verify all expenses are on or after start date
        for (ExpenseApiResponse expense : response.getBody()) {
            assertThat(expense.getDate()).isAfterOrEqualTo(LocalDate.of(2024, 3, 1));
        }
    }

    @Test
    void testFilterByDateRange_EndDateOnly() {
        // Test filtering with only end date (Requirement 6.3)
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/expenses"))
            .queryParam("endDate", "2024-01-31")
            .toUriString();

        ResponseEntity<List<ExpenseApiResponse>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ExpenseApiResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        
        // Verify all expenses are on or before end date
        for (ExpenseApiResponse expense : response.getBody()) {
            assertThat(expense.getDate()).isBeforeOrEqualTo(LocalDate.of(2024, 1, 31));
        }
    }

    @Test
    void testFilterByDateRange_InvalidRange() {
        // Test invalid date range (end before start) (Requirement 6.4)
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/expenses"))
            .queryParam("startDate", "2024-03-01")
            .queryParam("endDate", "2024-02-01")
            .toUriString();

        ResponseEntity<ErrorApiResponse> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            ErrorApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isNotNull();
    }

    @Test
    void testFilterBySingleCategory() {
        // Test filtering by single category (Requirement 7.1)
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/expenses"))
            .queryParam("categories", "TRAVEL")
            .toUriString();

        ResponseEntity<List<ExpenseApiResponse>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ExpenseApiResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        
        // Verify all expenses are TRAVEL category
        for (ExpenseApiResponse expense : response.getBody()) {
            assertThat(expense.getCategory()).isEqualTo("TRAVEL");
        }
    }

    @Test
    void testFilterByMultipleCategories() {
        // Test filtering by multiple categories (Requirement 7.2)
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/expenses"))
            .queryParam("categories", "FOOD", "OFFICE_SUPPLIES")
            .toUriString();

        ResponseEntity<List<ExpenseApiResponse>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ExpenseApiResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        
        // Verify all expenses match one of the specified categories
        for (ExpenseApiResponse expense : response.getBody()) {
            assertThat(expense.getCategory()).isIn("FOOD", "OFFICE_SUPPLIES");
        }
    }

    @Test
    void testFilterByInvalidCategory() {
        // Test filtering with invalid category (Requirement 7.3)
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/expenses"))
            .queryParam("categories", "INVALID_CATEGORY")
            .toUriString();

        ResponseEntity<ErrorApiResponse> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            ErrorApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("category");
    }

    @Test
    void testFilterByStatus() {
        // Test filtering by status (Requirement 8.1)
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/expenses"))
            .queryParam("status", "APPROVED")
            .toUriString();

        ResponseEntity<List<ExpenseApiResponse>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ExpenseApiResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(3);
        
        // Verify all expenses have APPROVED status
        for (ExpenseApiResponse expense : response.getBody()) {
            assertThat(expense.getStatus()).isEqualTo("APPROVED");
        }
    }

    @Test
    void testFilterByInvalidStatus() {
        // Test filtering with invalid status (Requirement 8.3)
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/expenses"))
            .queryParam("status", "INVALID_STATUS")
            .toUriString();

        ResponseEntity<ErrorApiResponse> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            ErrorApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("status");
    }

    @Test
    void testFilterWithMultipleCriteria() {
        // Test combining date range, category, and status filters (Requirement 12.3)
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/expenses"))
            .queryParam("startDate", "2024-01-01")
            .queryParam("endDate", "2024-02-28")
            .queryParam("categories", "TRAVEL", "FOOD")
            .queryParam("status", "APPROVED")
            .toUriString();

        ResponseEntity<List<ExpenseApiResponse>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ExpenseApiResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        
        // Verify all expenses match all criteria
        for (ExpenseApiResponse expense : response.getBody()) {
            assertThat(expense.getDate()).isBetween(
                LocalDate.of(2024, 1, 1), 
                LocalDate.of(2024, 2, 28)
            );
            assertThat(expense.getCategory()).isIn("TRAVEL", "FOOD");
            assertThat(expense.getStatus()).isEqualTo("APPROVED");
        }
    }

    @Test
    void testFilterWithNoMatches() {
        // Test filter combination that returns no results
        String url = UriComponentsBuilder.fromHttpUrl(url("/api/expenses"))
            .queryParam("categories", "UTILITIES")
            .queryParam("status", "REJECTED")
            .toUriString();

        ResponseEntity<List<ExpenseApiResponse>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ExpenseApiResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void testGetAllCategories() {
        // Test retrieving all available categories (Requirement 5.3, 12.6)
        ResponseEntity<List<String>> response = restTemplate.exchange(
            url("/api/categories"),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<String>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsExactlyInAnyOrder(
            "TRAVEL", "FOOD", "OFFICE_SUPPLIES", "UTILITIES", "ENTERTAINMENT"
        );
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
