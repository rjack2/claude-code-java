# Project Development Guidelines

This document defines the engineering standards and architectural guidelines for this project.

# Technology Stack

* Java 25
* Spring Boot 4.1.0
* Maven
* H2 Database
* Spring Data JPA

# Architecture Guidelines

* Use separate Entity and DTO classes.
* Do not use the same model for database persistence and API communication.
* Create dedicated mapper classes to convert between Entity and DTO objects.
* Keep business logic in the service layer.

# Package Structure

Use the following package structure under `com.vinsguru`:

* `controller` - REST API endpoints.
* `dto` - Request and response DTO classes.
* `entity` - JPA entity classes.
* `repository` - Spring Data JPA repositories.
* `service` - Business logic and application services.
* `mapper` - Entity to DTO and DTO to Entity mapping classes.
* `exceptions` - Custom exception classes.
* `advice` - Global exception handling using `@RestControllerAdvice`.

# Coding Standards

* Use Java records for all DTO classes.
* Do not use Lombok.

# API Error Handling

* Use `@RestControllerAdvice` for centralized exception handling.
* Return `ProblemDetail` with appropriate HTTP status codes for API errors.

# User Interface

* Create a modern Single Page Application (SPA).
* The UI must communicate with the backend exclusively through REST APIs.