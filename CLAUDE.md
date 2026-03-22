# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

NewGen Insurance Agent Portal — a web + mobile application for insurance agents/sales personnel to manage business, consent, and customer portfolios. The project is greenfield; see `problem-statement.md` for full user stories and acceptance criteria.

## Architecture

- **Microservice architecture** — each service handles a distinct domain (e.g., agent registration, login/auth, OTP verification)
- Services communicate via **REST APIs**
- **Backend:** Spring Boot (Spring Data, Spring REST, Spring Cloud Consul)
- **Frontend:** React
- **Database:** MySQL
- Service discovery via **Consul**

## API Conventions

- All endpoints must be prefixed with `/newgen`
- Use Swagger for API documentation
- DTOs for all request/response payloads — never expose entities directly
- Use **ModelMapper** for entity↔DTO conversion
- Bean validation on all inputs; custom validators for complex rules
- Null/empty parameter error format: `"Please provide a valid {attribute name}"`
- Date/time values must not start with zero

## Microservice Communication

- **Circuit Breaker** on all critical services: 3s timeout, 50% error threshold, opens after 10 consecutive failures, stays open 60s
- Fallback returns a generic message to the client
- Each microservice must support **2 instances with load balancing**

## Backend Conventions

- **Lombok** for models and logging
- Lambdas and Streams for business logic
- Layered package structure: controller, service, DTO, entity, repository, etc.
- Spring dependency injection throughout
- Centralized exception handling (translate exceptions → HTTP responses)
- **LoggingAspect** for service exception logging
- Passwords hashed with **bcrypt**

## Testing

- JUnit + Mockito for all service methods
- Minimum **80% code coverage**

## Code Quality (SonarQube Targets)

| Metric             | Minimum |
|--------------------|---------|
| Security           | A       |
| Reliability        | A       |
| Issues             | ≤ 5     |
| Coverage           | ≥ 80%   |
| Duplications       | ≤ 3%    |
| Security Hotspots  | A       |
