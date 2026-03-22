# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

NewGen Insurance Agent Portal — a web + mobile application for insurance agents/sales personnel to manage business, consent, and customer portfolios. The project is greenfield; see `problem-statement.md` for full user stories and acceptance criteria.

**Stack**: Java 17, Spring Boot 3.2.5, Spring Cloud 2023.0.1, React 19 + Vite, MySQL 8.0, Consul 1.18

## Build & Run Commands

### Infrastructure (MySQL + Consul)
```bash
docker-compose up -d          # Start MySQL (3306) + Consul (8500)
docker-compose down            # Stop infrastructure
```

MySQL credentials: `root`/`root`. Databases (`newgen_agent_db`, `newgen_otp_db`) are created by `init-db.sql` on first container start.

### Backend (each service is an independent Maven project)
```bash
cd agent-service && mvn clean install   # Build agent-service
cd otp-service && mvn clean install     # Build otp-service
cd login-service && mvn clean install   # Build login-service
cd gateway-service && mvn clean install     # Build gateway-service

cd agent-service && mvn spring-boot:run                                          # Run agent-service (8081)
cd otp-service && mvn spring-boot:run                                            # Run otp-service (8083)
cd login-service && mvn spring-boot:run                                          # Run login-service (8085)
cd gateway-service && mvn spring-boot:run                                            # Run gateway-service (8080)
```

### Second instances (load balancing)
```bash
cd agent-service && mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8082"
cd otp-service && mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8084"
cd login-service && mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8086"
```

### Testing
```bash
cd agent-service && mvn test                                                             # Run agent-service tests
cd otp-service && mvn test                                                               # Run otp-service tests
cd login-service && mvn test                                                             # Run login-service tests
cd agent-service && mvn test -Dtest=AgentServiceImplTest#testSignUpSuccess               # Run single test method
```

### Frontend
```bash
cd portal-ui
npm install
npm run dev      # Dev server on port 3000
npm run build    # Production build
npm run lint     # ESLint
npm run preview  # Preview production build
```

## Architecture

Four Spring Boot microservices + React SPA:

- **gateway-service** (8080) — Spring Cloud Gateway. Single entry point; routes all `/newgen/**` traffic to downstream services via Consul service discovery (`lb://` URIs). Includes a global logging filter.
- **agent-service** (8081/8082) — core service: agent registration, agency code validation, consent, credential setup. Owns `newgen_agent_db` MySQL database.
- **otp-service** (8083/8084) — OTP generation/validation/resend. Owns `newgen_otp_db` MySQL database. Calls agent-service to get agent name.
- **login-service** (8085/8086) — stateless login (password or MPIN). No database. Calls agent-service to verify credentials.
- **portal-ui** (3000) — React 19 + Vite SPA. Vite dev proxy routes all `/newgen/**` → gateway-service (8080).

### Inter-service communication
- otp-service and login-service call agent-service via `@LoadBalanced RestTemplate` through Consul service discovery
- Circuit breaker clients live in `{service}/client/AgentServiceClient.java` using Resilience4j `@CircuitBreaker`
- Fallback methods return generic "service unavailable" responses

### Package layout (consistent across all services)
```
com.newgen.{agent|otp|login}/
├── controller/        # REST controllers
├── service/           # Interface + Impl
├── dto/               # Request/response DTOs
├── entity/            # JPA entities (agent-service, otp-service only)
├── repository/        # Spring Data JPA repos (agent-service, otp-service only)
├── exception/         # GlobalExceptionHandler + custom exceptions
├── aspect/            # LoggingAspect (AOP exception logging)
├── config/            # AppConfig (ModelMapper, RestTemplate, BCryptPasswordEncoder beans)
├── client/            # Inter-service clients with circuit breaker (login-service, otp-service only)
└── validator/         # Custom annotation validators (agent-service only: @ValidPassword, @ValidMpin)
```

### Test location
All tests are at `{service}/src/test/java/com/newgen/{service}/service/{Service}ImplTest.java`. Tests use JUnit 5 + Mockito with `@Mock` repositories and `@InjectMocks` service impls.

## API Conventions

- All endpoints prefixed with `/newgen`
- Swagger UI: `http://localhost:{port}/swagger-ui.html`
- DTOs for all request/response payloads — never expose entities directly
- Use **ModelMapper** for entity↔DTO conversion
- Bean validation on all inputs; custom annotation validators for password (`^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@#$%^&+=!])\S{8,}$`) and MPIN (`^\d{4}$`)
- Null/empty parameter error format: `"Please provide a valid {attribute name}"`
- Date/time values must not start with zero
- Error response shape: `{ timestamp, status, message }`

### Endpoint Reference

| Service | Method | Path | Purpose |
|---------|--------|------|---------|
| agent-service | POST | `/newgen/agents/signup` | Register agent (201) |
| agent-service | POST | `/newgen/agents/validate-agency-code` | Validate agency code |
| agent-service | POST | `/newgen/agents/consent` | Record consent |
| agent-service | POST | `/newgen/agents/set-password` | Set login password |
| agent-service | POST | `/newgen/agents/set-mpin` | Set MPIN |
| agent-service | GET | `/newgen/agents/{agencyCode}` | Get agent by agency code |
| agent-service | POST | `/newgen/agents/verify-password` | Verify password (called by login-service) |
| agent-service | POST | `/newgen/agents/verify-mpin` | Verify MPIN (called by login-service) |
| otp-service | POST | `/newgen/otp/generate` | Generate OTP (201) |
| otp-service | POST | `/newgen/otp/validate` | Validate OTP |
| otp-service | POST | `/newgen/otp/resend` | Resend OTP |
| login-service | POST | `/newgen/login/password` | Login with password |
| login-service | POST | `/newgen/login/mpin` | Login with MPIN |

### OTP Behaviour
- 6-digit numeric OTP, expires after **5 minutes**
- Max **2 resends** with a **5-minute interval** between resends
- OTP is returned in the response body (no email/SMS integration) — use this for testing

## Microservice Communication

- **Resilience4j Circuit Breaker**: sliding window 10, 50% failure threshold, 3s slow-call timeout, 60s open-state wait, min 10 calls before evaluation
- Fallback returns generic "service temporarily unavailable" message
- Each microservice supports **2 instances with load balancing** via Consul

## Backend Conventions

- **Lombok** (`@Data`, `@Builder`, `@Slf4j`) for models and logging
- Lambdas and Streams for business logic
- Spring dependency injection throughout
- Centralized exception handling via `@RestControllerAdvice` GlobalExceptionHandler
- **LoggingAspect** with `@AfterThrowing` on all service methods
- Passwords and MPINs hashed with **bcrypt** (via `BCryptPasswordEncoder` bean)

## Frontend Conventions

- **Bootstrap 5** for styling — utility classes applied directly in JSX, no per-component CSS modules
- Shared components in `src/components/common/` (Button, FormInput, Logo, Stepper, SuccessScreen)
- Constants layer in `src/constants/` centralizes messages, validation rules, role definitions
- API calls through `src/services/` (Axios wrappers for agent, otp, login)
- No state management library — React Router `state` for the linear signup flow
- Route flow: `/` → `/signup` → `/otp-verification` → `/set-credential` → `/set-password` or `/set-mpin` → `/login` → `/home`

## Testing

- JUnit 5 + Mockito for all service methods — 32 total (18 agent-service, 8 otp-service, 6 login-service)
- Minimum **80% code coverage**
- Postman collection (`NewGen-Agent-Portal.postman_collection.json`) has 22 requests for end-to-end flow testing

## Seed Data

Four pre-loaded test agents (from `agent-service/src/main/resources/data.sql`, loaded via Hibernate `spring.sql.init.mode=always`):

| Agency Code | Name         | Role  |
|-------------|--------------|-------|
| AG001       | Rajesh Kumar | AGENT |
| AG002       | Priya Sharma | DO    |
| AG003       | Amit Patel   | CLIA  |
| AG004       | Sneha Reddy  | LICA  |

## Code Quality (SonarQube Targets)

| Metric             | Minimum |
|--------------------|---------|
| Security           | A       |
| Reliability        | A       |
| Issues             | ≤ 5     |
| Coverage           | ≥ 80%   |
| Duplications       | ≤ 3%    |
| Security Hotspots  | A       |
