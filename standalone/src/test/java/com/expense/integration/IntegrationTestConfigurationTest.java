package com.expense.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Integration test to verify the base integration test configuration.
 * 
 * This test validates that:
 * - The Spring Boot application starts successfully
 * - The test database is configured correctly
 * - TestRestTemplate is available for HTTP requests
 * - The random port is assigned correctly
 */
class IntegrationTestConfigurationTest extends BaseIntegrationTest {

    @Test
    void contextLoads() {
        // Verify that the application context loads successfully
        assertThat(restTemplate).isNotNull();
        assertThat(expenseRepository).isNotNull();
        assertThat(port).isGreaterThan(0);
    }

    @Test
    void testDatabaseIsEmpty() {
        // Verify that the database is cleaned up before each test
        long count = expenseRepository.count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    void testBaseUrlHelper() {
        // Verify that the URL helper method works correctly
        String apiUrl = url("/api/expenses");
        assertThat(apiUrl).isEqualTo("http://localhost:" + port + "/api/expenses");
    }

    @Test
    void testCategoriesEndpointIsAccessible() {
        // Verify that the API is accessible through TestRestTemplate
        ResponseEntity<String[]> response = restTemplate.getForEntity(
            url("/api/categories"),
            String[].class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(5); // 5 predefined categories
    }
}
