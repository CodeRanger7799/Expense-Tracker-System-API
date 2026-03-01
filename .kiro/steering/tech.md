# Technology Stack

## Core Technologies

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Build Tool**: Maven (multi-module project)
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA with Hibernate
- **API Specification**: OpenAPI 3.0
- **Code Generation**: openapi-generator-maven-plugin 7.0.1
- **Object Mapping**: MapStruct 1.5.5
- **Validation**: Jakarta Bean Validation 3.0.2

## Key Libraries

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- H2 Database
- MapStruct for DTO/entity mapping
- Swagger Annotations 2.2.19

## Common Commands

### Build Commands

```bash
# Build entire project from root
mvn clean install

# Build specific module
cd <module-name>
mvn clean install

# Skip tests during build
mvn clean install -DskipTests
```

### Run Commands

```bash
# Run application from root
mvn spring-boot:run -pl standalone

# Run from standalone module
cd standalone
mvn spring-boot:run

# Run JAR directly
java -jar standalone/target/standalone-1.0.0-SNAPSHOT.jar
```

### Test Commands

```bash
# Run all tests
mvn test

# Run tests for specific module
cd <module-name>
mvn test
```

### Code Generation

The `api` module uses openapi-generator-maven-plugin to generate API interfaces and models from the OpenAPI specification. Generation happens automatically during the Maven build process.

```bash
# Regenerate API code
cd api
mvn clean compile
```

## Development Environment

- **Server Port**: 8080
- **H2 Console**: http://localhost:8080/h2-console
- **Database URL**: jdbc:h2:mem:expensedb
- **Database Credentials**: username=sa, password=(empty)

## Configuration

Application configuration is in `standalone/src/main/resources/application.properties`:
- Database settings (H2 in-memory)
- JPA/Hibernate settings (ddl-auto=create-drop, show-sql=true)
- Logging levels (DEBUG for com.expense package)
