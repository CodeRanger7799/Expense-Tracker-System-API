# 💰 Expense Management System

> **Built entirely by [Kiro AI](https://kiro.ai)** - An intelligent AI-powered IDE that transforms ideas into production-ready code.

A production-ready, multi-module Spring Boot application for tracking and managing financial expenses with comprehensive reporting capabilities. This project demonstrates modern Java development practices, clean architecture, and API-first design principles.

---

## ✨ Features

- **Complete CRUD Operations** - Create, read, update, and delete expense records
- **Advanced Filtering** - Filter expenses by date range, category, and status
- **Intelligent Reporting** - Generate aggregated expense reports with category-wise breakdowns
- **Category Management** - Organize expenses across multiple categories (Travel, Food, Office Supplies, Utilities, Entertainment)
- **Status Tracking** - Track expense approval workflow (Pending, Approved, Rejected)
- **API-First Design** - OpenAPI 3.0 specification with auto-generated interfaces
- **Comprehensive Testing** - Full integration test suite included

---

## 🏗️ Architecture

This project follows a **layered, multi-module architecture** with clear separation of concerns:

```
expense-management-system/
├── 📋 api/                 # OpenAPI specification & generated interfaces
├── 📦 model/               # Domain entities, DTOs, enums, exceptions
├── 🗄️  dbadapter/          # Data access layer (Spring Data JPA)
├── ⚙️  management/          # Business logic services
├── 🎯 facade/              # REST controllers & API mappers
└── 🚀 standalone/          # Application entry point & configuration
```

### Module Dependencies Flow

```
standalone
    ├── facade
    │   ├── management
    │   │   ├── dbadapter
    │   │   │   └── model
    │   │   └── model
    │   ├── api
    │   └── model
    └── api
```

**Key Principles:**
- No circular dependencies
- Unidirectional dependency flow
- Foundation modules (`api`, `model`) have zero internal dependencies
- Each layer depends only on layers below it

---

## 🛠️ Technology Stack

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![H2](https://img.shields.io/badge/H2-Database-0000BB?style=for-the-badge&logo=h2&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-6BA539?style=for-the-badge&logo=openapi-initiative&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)

</div>

| Category | Technology |
|----------|-----------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.2.0 |
| **Build Tool** | Maven (Multi-module) |
| **Database** | H2 (In-memory) |
| **ORM** | Spring Data JPA + Hibernate |
| **API Spec** | OpenAPI 3.0 |
| **Code Generation** | openapi-generator-maven-plugin 7.0.1 |
| **Object Mapping** | MapStruct 1.5.5 |
| **Validation** | Jakarta Bean Validation 3.0.2 |
| **Documentation** | Swagger Annotations 2.2.19 |

---

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Build the Project

```bash
# Build all modules
mvn clean install

# Build without tests (faster)
mvn clean install -DskipTests

# Build specific module
cd <module-name>
mvn clean install
```

### Run the Application

```bash
# Option 1: Run from project root
mvn spring-boot:run -pl standalone

# Option 2: Run from standalone module
cd standalone
mvn spring-boot:run

# Option 3: Run the JAR
java -jar standalone/target/standalone-1.0.0-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**

### Access H2 Console

- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:expensedb`
- **Username**: `sa`
- **Password**: _(leave empty)_

---

## 📡 API Endpoints

### Expenses

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/expenses` | Create a new expense |
| `GET` | `/api/expenses` | Get all expenses (with optional filters) |
| `GET` | `/api/expenses/{id}` | Get expense by ID |
| `PUT` | `/api/expenses/{id}` | Update an expense |
| `DELETE` | `/api/expenses/{id}` | Delete an expense |

### Categories

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/categories` | Get all available categories |

### Reports

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/reports` | Generate expense report with aggregations |

### Query Parameters (Filtering)

- `startDate` - Filter from date (inclusive)
- `endDate` - Filter to date (inclusive)
- `categories` - Filter by categories (comma-separated)
- `status` - Filter by status (PENDING, APPROVED, REJECTED)

---

## 📦 Module Details

### 📋 api
**Purpose**: API contract definition and code generation

- Contains OpenAPI specification: `expense-api.yaml`
- Auto-generates API interfaces during build
- Generates request/response models
- Output: `target/generated-sources/openapi/`

### 📦 model
**Purpose**: Core domain models and data structures

- `entity/` - JPA entities with database mappings
- `dto/` - Data Transfer Objects for service layer
- `enums/` - Category and Status enumerations
- `domain/` - Domain models (e.g., ExpenseReport)
- `exception/` - Custom business exceptions

### 🗄️ dbadapter
**Purpose**: Data persistence layer

- Spring Data JPA repositories
- Custom query methods with `@Query`
- Database access abstraction
- Example: `ExpenseRepository.findByFilters()`

### ⚙️ management
**Purpose**: Business logic and orchestration

- Service layer with business rules
- Input validation and error handling
- Transaction management
- Examples: `ExpenseService`, `ReportService`

### 🎯 facade
**Purpose**: API presentation layer

- REST controllers implementing generated API interfaces
- MapStruct mappers for model conversion
- Global exception handling
- API model ↔ DTO transformation

### 🚀 standalone
**Purpose**: Application bootstrap and configuration

- Spring Boot main application class
- Configuration files (`application.properties`)
- Integration tests
- Executable JAR packaging

---

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Tests for Specific Module

```bash
cd <module-name>
mvn test
```

### Integration Tests

Located in `standalone/src/test/java/com/expense/integration/`:

- `BaseIntegrationTest` - Base test configuration
- `ExpenseWorkflowIntegrationTest` - End-to-end expense workflows
- `ExpenseFilteringIntegrationTest` - Filter functionality tests
- `ReportGenerationIntegrationTest` - Report generation tests

All integration tests use real Spring context with H2 in-memory database.

---

## 📝 API Documentation

The complete API specification is available in:
```
api/src/main/resources/openapi/expense-api.yaml
```

A Postman collection is also included:
```
postman-collection.json
```

Import this into Postman to test all API endpoints with pre-configured requests.

---

## 🎯 Design Patterns & Best Practices

This codebase demonstrates:

- **API-First Development** - OpenAPI specification drives implementation
- **Layered Architecture** - Clear separation of concerns
- **Dependency Injection** - Constructor-based injection throughout
- **DTO Pattern** - Separation of API models, DTOs, and entities
- **Repository Pattern** - Data access abstraction
- **Mapper Pattern** - MapStruct for object transformations
- **Exception Handling** - Centralized error handling with custom exceptions
- **Validation** - Jakarta Bean Validation at multiple layers
- **Immutable Configuration** - Externalized configuration
- **Integration Testing** - Comprehensive test coverage

---

## 🔧 Configuration

Application settings in `standalone/src/main/resources/application.properties`:

```properties
# Server
server.port=8080

# Database (H2 In-Memory)
spring.datasource.url=jdbc:h2:mem:expensedb
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Logging
logging.level.com.expense=DEBUG
```

---

## 📚 Package Structure

All packages follow the convention: `com.expense.<module>.<layer>`

```
com.expense.model.entity          # JPA entities
com.expense.model.dto             # Data Transfer Objects
com.expense.model.enums           # Enumerations
com.expense.dbadapter.repository  # Repositories
com.expense.management.service    # Business services
com.expense.facade.controller     # REST controllers
com.expense.facade.mapper         # MapStruct mappers
```

---

## 🤖 Built with Kiro AI

This entire codebase was generated by **[Kiro](https://kiro.ai)**, an AI-powered IDE that understands your requirements and builds production-ready applications. From architecture design to implementation, testing, and documentation - Kiro handled it all.

**What Kiro Built:**
- ✅ Multi-module Maven project structure
- ✅ OpenAPI specification and code generation setup
- ✅ Complete domain model with JPA entities
- ✅ Repository layer with custom queries
- ✅ Service layer with business logic and validation
- ✅ REST controllers with proper error handling
- ✅ MapStruct mappers for object transformations
- ✅ Comprehensive integration test suite
- ✅ Configuration and documentation
- ✅ This beautiful README!

---

## 📄 License

This project is available for educational and demonstration purposes.

---

## 🙏 Acknowledgments

Built with ❤️ by **Kiro AI** - Transforming ideas into code, one project at a time.

**Learn more**: [kiro.ai](https://kiro.ai)
