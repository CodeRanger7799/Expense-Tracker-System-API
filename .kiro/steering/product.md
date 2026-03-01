# Product Overview

Expense Management System - A RESTful API application for tracking and managing financial expenses with reporting capabilities.

## Core Features

- Create, read, update, and delete expense records
- Filter expenses by date range, category, and status
- Generate aggregated expense reports with category subtotals
- Categorize expenses (TRAVEL, FOOD, OFFICE_SUPPLIES, UTILITIES, ENTERTAINMENT)
- Track expense status (PENDING, APPROVED, REJECTED)

## API Design

The system follows an OpenAPI-first approach with the specification defined in `api/src/main/resources/openapi/expense-api.yaml`. All API interfaces and models are generated from this specification.

## Data Model

Expenses contain:
- Amount (BigDecimal for precision)
- Date (LocalDate, cannot be in the future)
- Category (enum)
- Description (max 500 characters)
- Status (enum)
- Timestamps (createdAt, updatedAt)
