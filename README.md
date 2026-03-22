# NewGen Insurance Agent Portal

A full-stack microservice-based application for insurance agent registration, OTP verification, credential setup, and login.

## Tech Stack


| Layer        | Technologies                                                                   |
| ------------ | ------------------------------------------------------------------------------ |
| **Backend**  | Java 17, Spring Boot 3.2.5, Spring Data JPA, Spring Cloud Consul, Resilience4j |
| **Frontend** | React 19, Vite, React Router, Axios                                            |
| **Database** | MySQL 8.0                                                                      |
| **Infra**    | Docker, Consul (service discovery + load balancing)                            |


## Architecture

```
┌──────────────┐      ┌───────────────────────────────────────────────┐
│  React SPA   │      │              Backend (Spring Boot)            │
│  (port 3000) │─────▶│                                               │
│              │      │  ┌──────────────┐  ┌───────────┐  ┌────────┐  │
│  Welcome     │      │  │agent-service │  │otp-service│  │login-  │  │
│  AgencyCode  │      │  │ :8081/:8082  │  │:8083/:8084│  │service │  │
│  OTP Verify  │      │  │              │◀─│           │  │:8085/  │  │
│  Set Creds   │      │  │  MySQL       │  │  MySQL    │  │:8086   │  │
│  Login       │      │  │  (agent_db)  │  │  (otp_db) │  │        │  │
│  Home        │      │  └──────────────┘  └───────────┘  └────────┘  │
└──────────────┘      │         ▲               ▲              ▲      │
                      │         └───────────────┘──────────────┘      │
                      │                    Consul                     │
                      └───────────────────────────────────────────────┘
```


| Service           | Port(s)     | Description                                                                          |
| ----------------- | ----------- | ------------------------------------------------------------------------------------ |
| **frontend**      | 3000        | React SPA — signup flow, login, home                                                 |
| **agent-service** | 8081 / 8082 | Agent registration, agency code validation, consent, credential setup & verification |
| **otp-service**   | 8083 / 8084 | OTP generation, validation, resend logic                                             |
| **login-service** | 8085 / 8086 | Login via password or MPIN (calls agent-service)                                     |


## Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+
- Docker & Docker Compose

## Getting Started

### 1. Start Infrastructure (MySQL + Consul)

```bash
docker-compose up -d
```

This starts:

- **MySQL** on port `3306` (creates `newgen_agent_db` and `newgen_otp_db` databases automatically)
- **Consul** on port `8500` (UI available at [http://localhost:8500](http://localhost:8500))

### 2. Build & Start Backend

```bash
mvn clean install

# Terminal 1 - Agent Service
mvn spring-boot:run -pl agent-service

# Terminal 2 - OTP Service
mvn spring-boot:run -pl otp-service

# Terminal 3 - Login Service
mvn spring-boot:run -pl login-service
```

### 3. Start Frontend

```bash
cd frontend
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

### 4. (Optional) Start Second Instances for Load Balancing

```bash
mvn spring-boot:run -pl agent-service -Dspring-boot.run.arguments="--server.port=8082"
mvn spring-boot:run -pl otp-service -Dspring-boot.run.arguments="--server.port=8084"
mvn spring-boot:run -pl login-service -Dspring-boot.run.arguments="--server.port=8086"
```

## Frontend

### Project Structure

```
frontend/src/
├── components/
│   ├── common/              # Reusable UI components
│   │   ├── Button           # Primary/secondary/link variants with loading state
│   │   ├── FormInput        # Label, input, error/success message
│   │   ├── Logo             # App logo header
│   │   ├── Stepper          # Step progress indicator
│   │   └── SuccessScreen    # Checkmark + message + CTA
│   ├── Welcome/             # US01 — Role selection (Agent/DO/CLIA/LICA)
│   ├── AgencyCode/          # US02 — Agency code validation + consent
│   ├── OtpVerification/     # US03 — 6-digit OTP with resend logic
│   ├── SetCredential/       # Credential type chooser (Password vs MPIN)
│   ├── SetPassword/         # US04 — Password setup with strength checklist
│   ├── SetMpin/             # US05 — 4-digit MPIN setup
│   ├── Login/               # US06 — Login with Password or MPIN tabs
│   └── Home/                # Post-login welcome screen
├── constants/               # Roles, messages, validation rules
├── services/                # Axios API layer (agent, otp, login)
├── styles/                  # Global CSS only (reset, container, typography)
├── App.jsx                  # Router configuration
└── main.jsx                 # Entry point
```

### Design Decisions

- **CSS Modules** per component for style encapsulation — no class name collisions
- **Shared components** (`Button`, `FormInput`, `Logo`, `Stepper`, `SuccessScreen`) eliminate duplication
- **Constants layer** centralizes error messages, validation rules, and role definitions
- **Vite dev proxy** routes `/newgen/`* to backend services — no CORS issues in development
- **No state management library** — React Router `state` is sufficient for this linear signup flow

## API Endpoints

All endpoints are prefixed with `/newgen`. Swagger UI is available at:

- Agent Service: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- OTP Service: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
- Login Service: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

### Agent Service (port 8081)


| Method | Endpoint                              | Description                  |
| ------ | ------------------------------------- | ---------------------------- |
| POST   | `/newgen/agents/signup`               | Register new agent with role |
| POST   | `/newgen/agents/validate-agency-code` | Validate an agency code      |
| POST   | `/newgen/agents/consent`              | Record agent consent         |
| POST   | `/newgen/agents/set-password`         | Set password (bcrypt)        |
| POST   | `/newgen/agents/set-mpin`             | Set 4-digit MPIN             |
| GET    | `/newgen/agents/{agencyCode}`         | Get agent details            |


### OTP Service (port 8083)


| Method | Endpoint               | Description                         |
| ------ | ---------------------- | ----------------------------------- |
| POST   | `/newgen/otp/generate` | Generate 6-digit OTP (5 min expiry) |
| POST   | `/newgen/otp/validate` | Validate OTP                        |
| POST   | `/newgen/otp/resend`   | Resend OTP (max 2 resends)          |


### Login Service (port 8085)


| Method | Endpoint                 | Description                       |
| ------ | ------------------------ | --------------------------------- |
| POST   | `/newgen/login/password` | Login with agency code + password |
| POST   | `/newgen/login/mpin`     | Login with agency code + MPIN     |


## Testing

### Backend Unit Tests

```bash
mvn test
```

32 tests across all three services (18 + 8 + 6).

### Postman

Import `NewGen-Agent-Portal.postman_collection.json` into Postman. The collection contains 22 requests organized by user flow:

1. **Agent Registration** — signup, duplicate check, validation errors
2. **Agency Code & Consent** — validate code, record consent
3. **OTP Verification** — generate, validate, resend
4. **Password Setup** — set, mismatch, weak password
5. **MPIN Setup** — set, mismatch, invalid format
6. **Login** — password login, MPIN login, incorrect credentials

Run the collection sequentially using the Postman Collection Runner for a full end-to-end test.

## Seed Data

Agent service comes pre-loaded with 4 test agents:


| Agency Code | Name         | Role  |
| ----------- | ------------ | ----- |
| AG001       | Rajesh Kumar | AGENT |
| AG002       | Priya Sharma | DO    |
| AG003       | Amit Patel   | CLIA  |
| AG004       | Sneha Reddy  | LICA  |


## Stopping

```bash
# Stop microservices: Ctrl+C in each terminal

# Stop frontend: Ctrl+C

# Stop infrastructure
docker-compose down
```

