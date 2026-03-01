package com.expense.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.expense.dbadapter.repository.ExpenseRepository;

/**
 * Base integration test class for the Expense Management System.
 * 
 * This class provides common configuration for integration tests:
 * - Starts the full Spring Boot application context
 * - Uses a random port to avoid conflicts
 * - Configures an in-memory H2 test database
 * - Provides TestRestTemplate for HTTP requests
 * - Cleans up database between tests
 * 
 * All integration tests should extend this class.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected ExpenseRepository expenseRepository;

    protected String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        // Clean up database before each test
        expenseRepository.deleteAll();
    }

    /**
     * Helper method to construct full API URLs.
     * 
     * @param path the API path (e.g., "/api/expenses")
     * @return the full URL
     */
    protected String url(String path) {
        return baseUrl + path;
    }
}
