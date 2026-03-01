# Project Structure

## Multi-Module Architecture

This is a Maven multi-module project following a layered architecture pattern with clear separation of concerns.

```
expense-management-system/
├── pom.xml                 # Parent POM with dependency management
├── api/                    # OpenAPI specification and generated code
├── model/                  # Domain entities, DTOs, enums, exceptions
├── dbadapter/             # Data access layer (repositories)
├── management/            # Business logic layer (services)
├── facade/                # Presentation layer (controllers, mappers)
└── standalone/            # Application entry point and configuration
```

## Module Dependencies

```
standalone → facade → management → dbadapter → model
          → api
```

- **api** and **model** have no internal dependencies (foundation modules)
- Dependencies flow in one direction (no circular dependencies)
- Each module depends only on modules below it in the hierarchy

## Module Descriptions

### api
- Contains OpenAPI specification: `api/src/main/resources/openapi/expense-api.yaml`
- Generates API interfaces (ExpensesApi, CategoriesApi, ReportsApi)
- Generates API models (ExpenseApiRequest, ExpenseApiResponse, etc.)
- Generated code is in `target/generated-sources/openapi/`

### model
- **entity/**: JPA entities with @Entity annotations (e.g., Expense)
- **dto/**: Internal DTOs for service layer communication
- **enums/**: Category and Status enums
- **domain/**: Domain models (e.g., ExpenseReport)
- **exception/**: Custom exceptions (ExpenseNotFoundException, ValidationException)

### dbadapter
- **repository/**: Spring Data JPA repositories
- Contains custom query methods using @Query annotations
- Example: `ExpenseRepository` with `findByFilters()` method

### management
- **service/**: Business logic services
- Handles validation, business rules, and orchestration
- Example: `ExpenseService`, `ReportService`
- Services use repositories for data access and return DTOs

### facade
- **controller/**: REST controllers implementing generated API interfaces
- **mapper/**: MapStruct mappers for API model ↔ DTO conversion
- **exception/**: GlobalExceptionHandler for centralized error handling
- Controllers delegate to services and use mappers for conversion

### standalone
- Contains `ExpenseManagementApplication` with @SpringBootApplication
- Configuration files in `src/main/resources/`
- Integration tests in `src/test/java/`

## Package Naming Convention

All packages follow the pattern: `com.expense.<module-name>.<layer>`

Examples:
- `com.expense.model.entity`
- `com.expense.dbadapter.repository`
- `com.expense.management.service`
- `com.expense.facade.controller`

## Code Organization Patterns

### Controllers
- Implement generated API interfaces from the `api` module
- Use constructor injection for dependencies
- Delegate business logic to services
- Use mappers to convert between API models and internal DTOs

### Services
- Annotated with @Service
- Use constructor injection for repository dependencies
- Contain business logic and validation
- Return internal DTOs, not entities

### Repositories
- Extend Spring Data JPA interfaces (JpaRepository, JpaSpecificationExecutor)
- Use method naming conventions for simple queries
- Use @Query for complex queries

### Mappers
- Use MapStruct @Mapper annotation with componentModel = "spring"
- Define explicit mapping methods for API models ↔ DTOs
- Handle list conversions

### Entities
- Use JPA annotations (@Entity, @Table, @Column, etc.)
- Use BigDecimal for monetary amounts
- Use LocalDate for dates, LocalDateTime for timestamps
- Enums stored as STRING in database

## Testing Structure

Integration tests are in `standalone/src/test/java/com/expense/integration/`:
- `BaseIntegrationTest`: Base class with @SpringBootTest
- Feature-specific test classes extending BaseIntegrationTest
- Tests use real Spring context with H2 in-memory database
