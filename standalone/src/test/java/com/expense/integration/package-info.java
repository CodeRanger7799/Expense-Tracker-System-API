/**
 * Integration test package for the Expense Management System.
 * 
 * <h2>Overview</h2>
 * This package contains integration tests that verify the complete system behavior
 * by testing the full application stack from HTTP requests to database persistence.
 * 
 * <h2>Base Configuration</h2>
 * All integration tests should extend {@link BaseIntegrationTest}, which provides:
 * <ul>
 *   <li>Full Spring Boot application context with random port</li>
 *   <li>In-memory H2 test database with automatic cleanup</li>
 *   <li>TestRestTemplate for making HTTP requests</li>
 *   <li>Helper methods for constructing API URLs</li>
 * </ul>
 * 
 * <h2>Test Database</h2>
 * The test database is configured with:
 * <ul>
 *   <li>H2 in-memory database (jdbc:h2:mem:testdb)</li>
 *   <li>Automatic schema creation from JPA entities</li>
 *   <li>Database cleanup before each test method</li>
 *   <li>Isolated test execution (no shared state)</li>
 * </ul>
 * 
 * <h2>Example Usage</h2>
 * <pre>
 * class MyIntegrationTest extends BaseIntegrationTest {
 *     
 *     {@literal @}Test
 *     void testCreateExpense() {
 *         ExpenseApiRequest request = new ExpenseApiRequest();
 *         request.setAmount(BigDecimal.valueOf(100.00));
 *         request.setDate(LocalDate.now());
 *         request.setCategory("TRAVEL");
 *         request.setDescription("Flight ticket");
 *         
 *         ResponseEntity&lt;ExpenseApiResponse&gt; response = restTemplate.postForEntity(
 *             url("/api/expenses"),
 *             request,
 *             ExpenseApiResponse.class
 *         );
 *         
 *         assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
 *         assertThat(response.getBody().getId()).isNotNull();
 *     }
 * }
 * </pre>
 * 
 * <h2>Requirements Validated</h2>
 * Integration tests validate:
 * <ul>
 *   <li>Requirement 11.1: H2 in-memory database usage</li>
 *   <li>Requirement 11.2: Database schema initialization</li>
 *   <li>All API endpoints and HTTP status codes (Requirements 12.1-12.9)</li>
 *   <li>Complete workflows: create → retrieve → update → delete</li>
 *   <li>Error handling and validation across all layers</li>
 * </ul>
 * 
 * @see BaseIntegrationTest
 */
package com.expense.integration;
