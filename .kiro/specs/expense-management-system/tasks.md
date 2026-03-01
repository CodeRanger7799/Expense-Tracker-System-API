# Implementation Plan: Expense Management System

## Overview

This plan implements a multi-module Maven Spring Boot application with OpenAPI code generation, MapStruct model mapping, and H2 database persistence. The implementation follows a layered architecture: api (OpenAPI spec + generated code) → facade (REST controllers + MapStruct) → management (business logic) → dbadapter (JPA repositories) → model (shared POJOs).

## Tasks

- [x] 1. Set up multi-module Maven project structure
  - Create parent pom.xml with module declarations and dependency management
  - Create module directories: api, model, dbadapter, management, facade, standalone
  - Configure Spring Boot parent and Java 17+ compiler settings
  - Set up module dependencies according to design hierarchy
  - _Requirements: 11.1, 11.2, 12.1-12.9_

- [x] 2. Implement model module with domain entities and DTOs
  - [x] 2.1 Create Category and Status enums
    - Implement Category enum with values: TRAVEL, FOOD, OFFICE_SUPPLIES, UTILITIES, ENTERTAINMENT
    - Implement Status enum with values: PENDING, APPROVED, REJECTED
    - _Requirements: 5.1, 8.2_
  
  - [x] 2.2 Create Expense JPA entity
    - Define entity with fields: id, amount, date, category, description, status, createdAt, updatedAt
    - Add JPA annotations: @Entity, @Table, @Id, @GeneratedValue, @Column, @Enumerated
    - Configure BigDecimal precision (10,2) for amount field
    - Add validation constraints via column definitions
    - _Requirements: 1.1, 1.2, 11.1, 11.2_
  
  - [x] 2.3 Create internal DTOs
    - Implement ExpenseRequest with Jakarta validation annotations (@NotNull, @Positive, @PastOrPresent, @NotBlank, @Size)
    - Implement ExpenseResponse with all expense fields
    - Implement ExpenseReportResponse with totalAmount, expenseCount, categorySubtotals, date range, generatedAt
    - Implement ErrorResponse with errorCode, message, details list, timestamp
    - _Requirements: 1.2, 1.3, 1.4, 9.1, 9.2, 9.3, 10.2, 10.3, 10.4_
  
  - [x] 2.4 Create domain models and exception classes
    - Implement ExpenseReport domain model
    - Implement ExpenseNotFoundException with custom message
    - Implement ValidationException with error list support
    - _Requirements: 2.2, 3.4, 4.2, 10.1, 10.2, 10.3_

- [x] 3. Implement API module with OpenAPI specification
  - [x] 3.1 Create OpenAPI YAML specification
    - Define expense-api.yaml with OpenAPI 3.0 structure
    - Define schemas: ExpenseApiRequest, ExpenseApiResponse, ExpenseReportApiResponse, ErrorApiResponse
    - Define /api/expenses endpoints: POST (create), GET (list with filters), GET /{id} (retrieve), PUT /{id} (update), DELETE /{id} (delete)
    - Define /api/categories endpoint: GET (list categories)
    - Define /api/reports endpoint: GET (generate report with filters)
    - Add query parameters for filtering: startDate, endDate, categories (array), status
    - Specify request/response content types as application/json
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6, 12.7, 12.8_
  
  - [x] 3.2 Configure openapi-generator-maven-plugin
    - Add plugin to api module pom.xml with version 7.0.1
    - Configure inputSpec path to src/main/resources/openapi/expense-api.yaml
    - Set generatorName to "spring"
    - Set apiPackage to "com.expense.api" and modelPackage to "com.expense.api.model"
    - Enable interfaceOnly, useSpringBoot3, skipDefaultInterface options
    - Verify generated interfaces: ExpenseApi, CategoryApi, ReportApi
    - Verify generated models: ExpenseApiRequest, ExpenseApiResponse, ExpenseReportApiResponse, ErrorApiResponse
    - _Requirements: 12.1-12.9_

- [x] 4. Implement dbadapter module with Spring Data repositories
  - [x] 4.1 Create ExpenseRepository interface
    - Extend JpaRepository<Expense, Long>
    - Add method: findAllByOrderByDateDesc() for date-ordered retrieval
    - Add method: findByDateBetween(LocalDate, LocalDate) for date range filtering
    - Add method: findByCategoryIn(List<Category>) for category filtering
    - Add method: findByStatus(Status) for status filtering
    - Add custom @Query method: findByFilters with optional parameters for combined filtering
    - _Requirements: 2.3, 2.4, 6.1, 6.2, 6.3, 7.1, 7.2, 8.1, 11.3_

- [ ] 5. Implement management module with business logic
  - [x] 5.1 Implement ExpenseService
    - Inject ExpenseRepository dependency
    - Implement createExpense: validate input, set status to PENDING, set timestamps, save to repository, convert entity to ExpenseResponse
    - Implement getExpenseById: find by ID, throw ExpenseNotFoundException if not found, convert to ExpenseResponse
    - Implement getAllExpenses: retrieve all ordered by date descending, convert to ExpenseResponse list
    - Implement filterExpenses: parse category/status strings to enums, call repository findByFilters, convert results
    - Implement updateExpense: find existing, validate input, update fields, update timestamp, save, convert to ExpenseResponse
    - Implement deleteExpense: find existing, throw ExpenseNotFoundException if not found, delete from repository
    - Implement getAllCategories: return Category enum values as strings
    - Implement validateExpense: check amount > 0, date not future, category valid, status valid, throw ValidationException on failure
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 5.1, 5.2, 5.3, 5.4, 6.1, 6.2, 6.3, 6.4, 7.1, 7.2, 7.3, 8.1, 8.3, 11.3_
  
  - [ ] 5.2 Write property tests for ExpenseService
    - **Property 1: Expense Creation Round-Trip** - validates Requirements 1.1, 1.5, 1.6, 2.1
    - **Property 3: Negative Amount Rejection** - validates Requirements 1.3, 3.3
    - **Property 4: Future Date Rejection** - validates Requirements 1.4
    - **Property 6: Retrieve All Returns Complete Set** - validates Requirements 2.3
    - **Property 7: Date Descending Order Invariant** - validates Requirements 2.4
    - **Property 8: Expense Update Round-Trip** - validates Requirements 3.1, 3.2, 3.5
    - **Property 9: Delete Removes Expense** - validates Requirements 4.1, 4.3
    - **Property 10: Invalid Category Rejection** - validates Requirements 5.2, 7.3
    - **Property 11: Category Filter Correctness** - validates Requirements 5.4, 7.1
    - **Property 12: Multi-Category Filter Correctness** - validates Requirements 7.2
    - **Property 13: Date Range Filter Inclusivity** - validates Requirements 6.1
    - **Property 14: Start Date Only Filter** - validates Requirements 6.2
    - **Property 15: End Date Only Filter** - validates Requirements 6.3
    - **Property 16: Invalid Date Range Rejection** - validates Requirements 6.4
    - **Property 17: Status Filter Correctness** - validates Requirements 8.1
    - **Property 18: Invalid Status Rejection** - validates Requirements 8.3
    - **Property 27: Persistence Round-Trip** - validates Requirements 11.3
  
  - [x] 5.3 Implement ReportService
    - Inject ExpenseService dependency
    - Implement generateReport: call filterExpenses with parameters, calculate totalAmount (sum), calculate expenseCount, calculate categorySubtotals (group by category), set date range and generatedAt, return ExpenseReportResponse
    - Implement calculateCategorySubtotals: group expenses by category, sum amounts per category, return Map<String, BigDecimal>
    - _Requirements: 9.1, 9.2, 9.3, 9.4_
  
  - [ ] 5.4 Write property tests for ReportService
    - **Property 19: Report Total Calculation** - validates Requirements 9.1
    - **Property 20: Report Count Calculation** - validates Requirements 9.2
    - **Property 21: Report Category Subtotals** - validates Requirements 9.3
    - **Property 22: Report Respects Filters** - validates Requirements 9.4

- [ ] 6. Implement facade module with REST controllers and MapStruct mappers
  - [x] 6.1 Create MapStruct mapper interfaces
    - Create ExpenseMapper interface with @Mapper(componentModel = "spring")
    - Add method: ExpenseRequest apiRequestToDto(ExpenseApiRequest)
    - Add method: ExpenseApiResponse dtoToApiResponse(ExpenseResponse)
    - Add method: List<ExpenseApiResponse> dtoListToApiResponseList(List<ExpenseResponse>)
    - Create ReportMapper interface with @Mapper(componentModel = "spring")
    - Add method: ExpenseReportApiResponse dtoToApiResponse(ExpenseReportResponse)
    - Configure MapStruct annotation processor in facade pom.xml
    - _Requirements: 12.8_
  
  - [x] 6.2 Implement ExpenseController
    - Annotate with @RestController
    - Implement ExpenseApi interface (generated from OpenAPI)
    - Inject ExpenseService and ExpenseMapper dependencies
    - Implement createExpense: convert API request to DTO, call service, convert response to API model, return 201 Created
    - Implement getExpenseById: call service, convert to API model, return 200 OK
    - Implement getAllExpenses: call service filterExpenses with query parameters, convert list to API models, return 200 OK
    - Implement updateExpense: convert API request to DTO, call service, convert response to API model, return 200 OK
    - Implement deleteExpense: call service, return 204 No Content
    - _Requirements: 1.6, 2.1, 2.2, 3.5, 4.3, 12.1, 12.2, 12.3, 12.4, 12.5, 12.8, 12.9_
  
  - [x] 6.3 Implement CategoryController
    - Annotate with @RestController
    - Implement CategoryApi interface (generated from OpenAPI)
    - Inject ExpenseService dependency
    - Implement getAllCategories: call service, return 200 OK with category list
    - _Requirements: 5.3, 12.6, 12.8, 12.9_
  
  - [x] 6.4 Implement ReportController
    - Annotate with @RestController
    - Implement ReportApi interface (generated from OpenAPI)
    - Inject ReportService and ReportMapper dependencies
    - Implement generateReport: call service with query parameters, convert to API model, return 200 OK
    - _Requirements: 9.5, 12.7, 12.8, 12.9_
  
  - [x] 6.5 Implement GlobalExceptionHandler
    - Annotate with @RestControllerAdvice
    - Add @ExceptionHandler for ExpenseNotFoundException: return ErrorApiResponse with 404 status
    - Add @ExceptionHandler for ValidationException: return ErrorApiResponse with 400 status and error details
    - Add @ExceptionHandler for MethodArgumentNotValidException: extract field errors, return ErrorApiResponse with 400 status
    - Add @ExceptionHandler for HttpMessageNotReadableException: return ErrorApiResponse with 400 status for invalid JSON
    - Ensure all error responses include errorCode, message, details (optional), and timestamp
    - _Requirements: 2.2, 3.4, 4.2, 10.1, 10.2, 10.3, 10.4, 12.9_
  
  - [ ] 6.6 Write property tests for API layer
    - **Property 2: Required Fields Validation** - validates Requirements 1.2, 10.2
    - **Property 5: Non-Existent Expense Returns 404** - validates Requirements 2.2, 3.4, 4.2
    - **Property 23: Report Returns 200** - validates Requirements 9.5
    - **Property 24: Invalid JSON Rejection** - validates Requirements 10.1
    - **Property 25: Invalid Data Type Rejection** - validates Requirements 10.3
    - **Property 26: Error Response Format Consistency** - validates Requirements 10.4
    - **Property 28: JSON Content Negotiation** - validates Requirements 12.8

- [x] 7. Implement standalone module with Spring Boot application
  - [x] 7.1 Create ExpenseManagementApplication class
    - Annotate with @SpringBootApplication
    - Configure scanBasePackages: com.expense.facade, com.expense.management, com.expense.dbadapter, com.expense.model
    - Implement main method with SpringApplication.run
    - _Requirements: 11.1, 11.2_
  
  - [x] 7.2 Create application.properties configuration
    - Configure H2 database: spring.datasource.url=jdbc:h2:mem:expensedb
    - Configure H2 console: spring.h2.console.enabled=true
    - Configure JPA: spring.jpa.hibernate.ddl-auto=create-drop
    - Configure server port: server.port=8080
    - Configure logging levels for debugging
    - _Requirements: 11.1, 11.2, 11.4_

- [x] 8. Checkpoint - Build and verify project structure
  - Run mvn clean install from parent directory
  - Verify all modules compile successfully
  - Verify OpenAPI code generation produces expected interfaces and models
  - Verify MapStruct generates mapper implementations
  - Ensure all tests pass, ask the user if questions arise

- [x] 9. Integration testing and end-to-end verification
  - [x] 9.1 Create integration test configuration
    - Set up @SpringBootTest with webEnvironment = RANDOM_PORT
    - Configure TestRestTemplate for HTTP requests
    - Set up test database with @AutoConfigureTestDatabase
    - _Requirements: 11.1, 11.2_
  
  - [x] 9.2 Write integration tests for complete workflows
    - Test create → retrieve → update → delete workflow
    - Test filtering with multiple criteria combinations
    - Test report generation with various filter combinations
    - Test error scenarios: 404, 400 validation errors, invalid JSON
    - Verify HTTP status codes match requirements
    - Verify response formats match OpenAPI specification
    - _Requirements: All requirements 1-12_

- [x] 10. Final checkpoint - Complete system verification
  - Run mvn clean install with all tests
  - Start application and verify H2 console access
  - Manually test all API endpoints using curl or Postman
  - Verify all 28 correctness properties are covered by tests
  - Ensure all tests pass, ask the user if questions arise

## Notes

- Tasks marked with `*` are optional property-based and integration tests
- Each task references specific requirements for traceability
- OpenAPI code generation happens during Maven compile phase
- MapStruct mapper implementations are generated at compile time
- H2 database schema is auto-created from JPA entity annotations
- All property tests should use jqwik library with minimum 100 iterations
- Property test comments must include: `// Feature: expense-management-system, Property N: [Title]`
