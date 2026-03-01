package com.expense.integration;

import com.expense.api.model.ExpenseApiRequest;
import com.expense.api.model.ExpenseApiResponse;
import com.expense.api.model.ErrorApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for complete CRUD workflows.
 * Tests Requirements: 1.1-1.6, 2.1-2.4, 3.1-3.5, 4.1-4.3, 10.1-10.4, 12.1-12.9
 */
class ExpenseWorkflowIntegrationTest extends BaseIntegrationTest {

    /**
     * Tests the complete CRUD workflow for expenses: create, retrieve, update, and delete.
     * Verifies that all operations work correctly in sequence and that the expense lifecycle
     * is properly managed from creation through deletion.
     * 
     * <p>Test Steps:</p>
     * <ol>
     *   <li>Create a new expense and verify HTTP 201 response with correct data</li>
     *   <li>Retrieve the created expense by ID and verify HTTP 200 response</li>
     *   <li>Update the expense and verify HTTP 200 response with updated data</li>
     *   <li>Delete the expense and verify HTTP 204 response</li>
     *   <li>Attempt to retrieve deleted expense and verify HTTP 404 response</li>
     * </ol>
     * 
     * <p>Requirements tested: 1.1, 1.5, 1.6, 2.1, 2.2, 3.1, 3.2, 3.5, 4.1, 4.3, 12.1, 12.2, 12.4, 12.5, 12.9</p>
     */
    @Test
    void testCompleteCreateRetrieveUpdateDeleteWorkflow() {
        // Step 1: Create an expense (Requirement 1.1, 1.5, 1.6, 12.1)
        ExpenseApiRequest createRequest = new ExpenseApiRequest();
        createRequest.setAmount(150.50);
        createRequest.setDate(LocalDate.now().minusDays(1));
        createRequest.setCategory("TRAVEL");
        createRequest.setDescription("Flight ticket to NYC");

        ResponseEntity<ExpenseApiResponse> createResponse = restTemplate.postForEntity(
            url("/api/expenses"),
            createRequest,
            ExpenseApiResponse.class
        );

        // Verify creation (Requirement 1.6, 12.9)
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        ExpenseApiResponse createdExpense = createResponse.getBody();
        assert createdExpense != null;
        assertThat(createdExpense.getId()).isNotNull();
        assertThat(createdExpense.getAmount()).isEqualTo(150.50);
        assertThat(createdExpense.getCategory()).isEqualTo("TRAVEL");
        assertThat(createdExpense.getStatus()).isEqualTo("PENDING");
        assertThat(createdExpense.getCreatedAt()).isNotNull();

        Long expenseId = createdExpense.getId();

        // Step 2: Retrieve the created expense (Requirement 2.1, 2.2, 12.2)
        ResponseEntity<ExpenseApiResponse> getResponse = restTemplate.getForEntity(
            url("/api/expenses/" + expenseId),
            ExpenseApiResponse.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        ExpenseApiResponse retrievedExpense = getResponse.getBody();
        assert retrievedExpense != null;
        assertThat(retrievedExpense.getId()).isEqualTo(expenseId);
        assertThat(retrievedExpense.getDescription()).isEqualTo("Flight ticket to NYC");

        // Step 3: Update the expense (Requirement 3.1, 3.2, 3.5, 12.4)
        ExpenseApiRequest updateRequest = new ExpenseApiRequest();
        updateRequest.setAmount(175.75);
        updateRequest.setDate(LocalDate.now().minusDays(1));
        updateRequest.setCategory("TRAVEL");
        updateRequest.setDescription("Flight ticket to NYC - Updated");
        updateRequest.setStatus("APPROVED");

        ResponseEntity<ExpenseApiResponse> updateResponse = restTemplate.exchange(
            url("/api/expenses/" + expenseId),
            HttpMethod.PUT,
            new HttpEntity<>(updateRequest),
            ExpenseApiResponse.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        ExpenseApiResponse updatedExpense = updateResponse.getBody();
        assert updatedExpense != null;
        assertThat(updatedExpense.getAmount()).isEqualTo(175.75);
        assertThat(updatedExpense.getDescription()).isEqualTo("Flight ticket to NYC - Updated");
        assertThat(updatedExpense.getStatus()).isEqualTo("APPROVED");
        assertThat(updatedExpense.getUpdatedAt()).isNotNull();

        // Step 4: Delete the expense (Requirement 4.1, 4.3, 12.5)
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            url("/api/expenses/" + expenseId),
            HttpMethod.DELETE,
            null,
            Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Step 5: Verify deletion - should return 404 (Requirement 2.2, 4.2)
        ResponseEntity<ErrorApiResponse> notFoundResponse = restTemplate.getForEntity(
            url("/api/expenses/" + expenseId),
            ErrorApiResponse.class
        );

        assertThat(notFoundResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(notFoundResponse.getBody()).isNotNull();
        ErrorApiResponse errorResponse = notFoundResponse.getBody();
        assert errorResponse != null;
        assertThat(errorResponse.getMessage()).contains("not found");
    }

    /**
     * Tests that retrieving all expenses returns them in descending order by date (newest first).
     * Creates multiple expenses with different dates and verifies the ordering in the response.
     * 
     * <p>Requirements tested: 2.3, 2.4</p>
     */
    @Test
    void testRetrieveAllExpensesInDescendingOrder() {
        // Create multiple expenses with different dates (Requirement 2.3, 2.4)
        createExpense(100.0, LocalDate.now().minusDays(5), "FOOD", "Lunch");
        createExpense(200.0, LocalDate.now().minusDays(2), "TRAVEL", "Taxi");
        createExpense(50.0, LocalDate.now().minusDays(10), "OFFICE_SUPPLIES", "Pens");

        ResponseEntity<List<ExpenseApiResponse>> response = restTemplate.exchange(
            url("/api/expenses"),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ExpenseApiResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        List<ExpenseApiResponse> expenses = response.getBody();
        assert expenses != null;
        assertThat(expenses).hasSize(3);

        // Verify descending order by date (Requirement 2.4)
        assertThat(expenses.get(0).getDate()).isAfter(expenses.get(1).getDate());
        assertThat(expenses.get(1).getDate()).isAfter(expenses.get(2).getDate());
    }

    /**
     * Tests error handling for operations on non-existent expenses.
     * Verifies that HTTP 404 (Not Found) is returned with proper error response format
     * for GET, PUT, and DELETE operations on non-existent expense IDs.
     * 
     * <p>Requirements tested: 2.2, 3.4, 4.2, 10.4</p>
     */
    @Test
    void testErrorScenario_NotFound() {
        // Test 404 for non-existent expense (Requirement 2.2, 3.4, 4.2, 10.4)
        ResponseEntity<ErrorApiResponse> getResponse = restTemplate.getForEntity(
            url("/api/expenses/99999"),
            ErrorApiResponse.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).isNotNull();
        ErrorApiResponse getError = getResponse.getBody();
        assert getError != null;
        assertThat(getError.getErrorCode()).isNotNull();
        assertThat(getError.getMessage()).isNotNull();
        assertThat(getError.getTimestamp()).isNotNull();

        // Test 404 for update on non-existent expense
        ExpenseApiRequest updateRequest = new ExpenseApiRequest();
        updateRequest.setAmount(100.0);
        updateRequest.setDate(LocalDate.now());
        updateRequest.setCategory("FOOD");
        updateRequest.setDescription("Test");

        ResponseEntity<ErrorApiResponse> updateResponse = restTemplate.exchange(
            url("/api/expenses/99999"),
            HttpMethod.PUT,
            new HttpEntity<>(updateRequest),
            ErrorApiResponse.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        // Test 404 for delete on non-existent expense
        ResponseEntity<ErrorApiResponse> deleteResponse = restTemplate.exchange(
            url("/api/expenses/99999"),
            HttpMethod.DELETE,
            null,
            ErrorApiResponse.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Tests validation error handling for invalid expense data.
     * Verifies that HTTP 400 (Bad Request) is returned with proper error messages
     * for expenses with negative amounts and future dates.
     * 
     * <p>Requirements tested: 1.3, 1.4, 3.3, 10.2</p>
     */
    @Test
    void testErrorScenario_ValidationErrors() {
        // Test negative amount (Requirement 1.3, 3.3, 10.2)
        ExpenseApiRequest negativeAmountRequest = new ExpenseApiRequest();
        negativeAmountRequest.setAmount(-50.0);
        negativeAmountRequest.setDate(LocalDate.now());
        negativeAmountRequest.setCategory("FOOD");
        negativeAmountRequest.setDescription("Invalid expense");

        ResponseEntity<ErrorApiResponse> negativeResponse = restTemplate.postForEntity(
            url("/api/expenses"),
            negativeAmountRequest,
            ErrorApiResponse.class
        );

        assertThat(negativeResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(negativeResponse.getBody()).isNotNull();
        ErrorApiResponse negativeError = negativeResponse.getBody();
        assert negativeError != null;
        assertThat(negativeError.getMessage()).isNotNull();

        // Test future date (Requirement 1.4, 10.2)
        ExpenseApiRequest futureDateRequest = new ExpenseApiRequest();
        futureDateRequest.setAmount(100.0);
        futureDateRequest.setDate(LocalDate.now().plusDays(5));
        futureDateRequest.setCategory("FOOD");
        futureDateRequest.setDescription("Future expense");

        ResponseEntity<ErrorApiResponse> futureResponse = restTemplate.postForEntity(
            url("/api/expenses"),
            futureDateRequest,
            ErrorApiResponse.class
        );

        assertThat(futureResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(futureResponse.getBody()).isNotNull();
    }

    /**
     * Tests error handling for malformed JSON in request body.
     * Verifies that HTTP 400 (Bad Request) is returned with proper error response
     * when the request contains invalid JSON syntax.
     * 
     * <p>Requirements tested: 10.1, 10.4</p>
     */
    @Test
    void testErrorScenario_InvalidJSON() {
        // Test invalid JSON (Requirement 10.1, 10.4)
        String invalidJson = "{invalid json content}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(invalidJson, headers);

        ResponseEntity<ErrorApiResponse> response = restTemplate.postForEntity(
            url("/api/expenses"),
            entity,
            ErrorApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        ErrorApiResponse errorResponse = response.getBody();
        assert errorResponse != null;
        assertThat(errorResponse.getErrorCode()).isNotNull();
        assertThat(errorResponse.getMessage()).isNotNull();
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    /**
     * Tests validation error handling for missing required fields.
     * Verifies that HTTP 400 (Bad Request) is returned with detailed error information
     * when required fields (date, category, description) are missing from the request.
     * 
     * <p>Requirements tested: 1.2, 10.2, 10.4</p>
     */
    @Test
    void testErrorScenario_MissingRequiredFields() {
        // Test missing required fields (Requirement 1.2, 10.2, 10.4)
        ExpenseApiRequest incompleteRequest = new ExpenseApiRequest();
        incompleteRequest.setAmount(100.0);
        // Missing date, category, and description

        ResponseEntity<ErrorApiResponse> response = restTemplate.postForEntity(
            url("/api/expenses"),
            incompleteRequest,
            ErrorApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        ErrorApiResponse errorResponse = response.getBody();
        assert errorResponse != null;
        assertThat(errorResponse.getDetails()).isNotNull();
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    /**
     * Tests JSON content type negotiation for API responses.
     * Verifies that the API correctly returns responses with application/json content type
     * as specified in the API contract.
     * 
     * <p>Requirements tested: 12.8</p>
     */
    @Test
    void testJSONContentNegotiation() {
        // Test JSON content type (Requirement 12.8)
        ExpenseApiRequest request = new ExpenseApiRequest();
        request.setAmount(100.0);
        request.setDate(LocalDate.now());
        request.setCategory("FOOD");
        request.setDescription("Test expense");

        ResponseEntity<ExpenseApiResponse> response = restTemplate.postForEntity(
            url("/api/expenses"),
            request,
            ExpenseApiResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        String contentType = response.getHeaders().getContentType() != null 
            ? response.getHeaders().getContentType().toString() 
            : "";
        assertThat(contentType).contains("application/json");
    }

    /**
     * Helper method to create an expense via the REST API.
     * 
     * @param amount the expense amount
     * @param date the expense date
     * @param category the expense category (e.g., "TRAVEL", "FOOD")
     * @param description the expense description
     * @return the created expense response, or null if creation failed
     */
    private ExpenseApiResponse createExpense(Double amount, LocalDate date, String category, String description) {
        ExpenseApiRequest request = new ExpenseApiRequest();
        request.setAmount(amount);
        request.setDate(date);
        request.setCategory(category);
        request.setDescription(description);

        ResponseEntity<ExpenseApiResponse> response = restTemplate.postForEntity(
            url("/api/expenses"),
            request,
            ExpenseApiResponse.class
        );

        return response.getBody();
    }
}
