# Account Request & Approval Management System

A Spring Boot + MySQL workflow system for employee account requests and approval tracking.

Employees submit requests for system/account access (e.g. VPN, AWS Console, GitHub Org). Managers review, approve, or reject each request. The system enforces role-based authorization and a strict state machine to keep the approval workflow consistent and auditable.

## Overview

This project simulates a common enterprise workflow: **request → review → decision**. It was built as a portfolio project to demonstrate backend fundamentals with Spring Boot — layered architecture, JPA relationships, role-based authorization, centralized exception handling, and containerized deployment.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5 (Spring Web, Spring Data JPA, Validation) |
| Database | MySQL 8 |
| Build Tool | Maven |
| Testing | JUnit 5, Mockito |
| Containerization | Docker, Docker Compose |

## Features

- Employees can submit account requests with a target system name and reason
- Managers can approve or reject pending requests with a review comment
- Employees can cancel their own pending requests
- **Duplicate-request prevention**: an employee cannot submit a second pending request for the same system while one is already pending
- **Role-based authorization**: only managers can approve/reject; only the original requester can cancel their own request
- **State machine enforcement**: requests can only transition out of `PENDING` once — no re-approving or re-rejecting a finalized request
- Centralized error handling with consistent JSON error responses and correct HTTP status codes
- Input validation on all write operations

## Architecture

```
Client (Postman / Frontend)
        │
        ▼
  Controller Layer      → HTTP request/response, input binding
        │
        ▼
    Service Layer        → business rules, authorization, state machine
        │
        ▼
  Repository Layer       → Spring Data JPA, database access
        │
        ▼
      MySQL
```

Requests and responses are decoupled from JPA entities via DTOs, and all business-rule violations are surfaced through custom exceptions caught by a global `@RestControllerAdvice`.

## User Roles & Workflow

| Role | Capabilities |
|---|---|
| `EMPLOYEE` | Submit requests, view own requests, cancel own pending requests |
| `MANAGER` | View all requests, approve/reject any pending request |

```
              ┌──────────► APPROVED   (final, by MANAGER)
PENDING ──────┼──────────► REJECTED   (final, by MANAGER, requires comment)
              └──────────► CANCELLED  (final, by the original requester only)
```

> This project uses a simulated authentication scheme (`X-User-Id` header) instead of full Spring Security/JWT. This was a deliberate scope decision — see [Design Decisions](#design-decisions) below.

## Database Schema

**`users`**

| Column | Type | Notes |
|---|---|---|
| id | BIGINT (PK, auto increment) | |
| name | VARCHAR(100) | |
| email | VARCHAR(150) | unique |
| role | VARCHAR(20) | `EMPLOYEE` or `MANAGER` |
| created_at | TIMESTAMP | |

**`account_requests`**

| Column | Type | Notes |
|---|---|---|
| id | BIGINT (PK, auto increment) | |
| requester_id | BIGINT (FK → users.id) | |
| system_name | VARCHAR(100) | |
| reason | VARCHAR(500) | |
| status | VARCHAR(20) | `PENDING` / `APPROVED` / `REJECTED` / `CANCELLED` |
| reviewed_by | BIGINT (FK → users.id, nullable) | |
| review_comment | VARCHAR(500), nullable | |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |
| reviewed_at | TIMESTAMP, nullable | |

## API Endpoints

All requests (except `GET /api/users`) require an `X-User-Id` header identifying the acting user.

| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | `/api/requests` | EMPLOYEE | Submit a new account request |
| GET | `/api/requests` | EMPLOYEE / MANAGER | List requests (employees see only their own; managers see all). Supports `?status=` filter |
| GET | `/api/requests/{id}` | EMPLOYEE / MANAGER | Get a single request (employees can only view their own) |
| PATCH | `/api/requests/{id}/approve` | MANAGER | Approve a pending request |
| PATCH | `/api/requests/{id}/reject` | MANAGER | Reject a pending request (requires a `comment`) |
| PATCH | `/api/requests/{id}/cancel` | EMPLOYEE | Cancel your own pending request |
| GET | `/api/users` | — | List all users (for testing purposes, since there is no signup/login flow) |

### Example: Create a request

```http
POST /api/requests
X-User-Id: 1
Content-Type: application/json

{
  "systemName": "VPN Access",
  "reason": "Need remote access to the internal network for development work"
}
```

**Response `201 Created`**

```json
{
  "id": 1,
  "requesterName": "Alice Employee",
  "systemName": "VPN Access",
  "reason": "Need remote access to the internal network for development work",
  "status": "PENDING",
  "reviewedByName": null,
  "reviewComment": null,
  "createdAt": "2026-07-02T02:37:12.19",
  "updatedAt": "2026-07-02T02:37:12.19",
  "reviewedAt": null
}
```

### Example: Reject a request

```http
PATCH /api/requests/2/reject
X-User-Id: 2
Content-Type: application/json

{
  "comment": "AWS account quota is full this quarter, please re-apply next quarter"
}
```

### Example error response

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "You already have a pending request for \"VPN Access\"",
  "timestamp": "2026-07-02T02:39:49.18"
}
```

## Getting Started

### Option A — Docker Compose (recommended, no local Java/MySQL setup needed)

**Requirements:** Docker Desktop

```bash
git clone https://github.com/alstonlee07/account-request-system.git
cd account-request-system
cp .env.example .env   # then fill in a password of your choice
docker-compose up --build
```

The app will be available at `http://localhost:8080`.

### Option B — Run locally

**Requirements:** Java 17, Maven, MySQL 8

1. Create a database:
   ```sql
   CREATE DATABASE account_request_db CHARACTER SET utf8mb4;
   ```
2. Set the following environment variables (or edit `src/main/resources/application.yaml` directly):
   ```
   DB_URL=jdbc:mysql://localhost:3306/account_request_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Taipei
   DB_USERNAME=root
   DB_PASSWORD=your_password
   ```
3. Run:
   ```bash
   ./mvnw spring-boot:run
   ```

### Seeding test users

There is no signup flow — seed a couple of test users directly:

```sql
INSERT INTO users (name, email, role, created_at) VALUES
('Alice Employee', 'alice@example.com', 'EMPLOYEE', NOW()),
('Bob Manager', 'bob@example.com', 'MANAGER', NOW());
```

## Testing

Run unit tests:

```bash
./mvnw test
```

API behavior was also manually verified end-to-end with Postman, covering:

- Successful request creation, approval, rejection, and cancellation
- Duplicate pending-request prevention
- Role-based authorization failures (`403`)
- Invalid state transitions (`409`)
- Validation failures (`400`)
- Not-found resources (`404`)

## Design Decisions

- **Simulated authentication via `X-User-Id` header** instead of full Spring Security/JWT — this let the project focus on the core workflow and state machine logic first. A real implementation would add proper authentication; the authorization *logic* itself (role checks, ownership checks) is already implemented at the service layer and would carry over directly.
- **DTOs are used for all API input/output** rather than exposing JPA entities directly — this prevents clients from setting fields like `status` during creation (mass-assignment protection) and avoids serialization issues with lazy-loaded relationships.
- **`ddl-auto: update`** is used for convenience in this project. A production setup would use a migration tool like Flyway for versioned, reviewable schema changes.

## Possible Future Improvements

- Replace simulated auth with Spring Security + JWT
- Add Flyway for database migrations
- Add pagination to list endpoints
- Add OpenAPI/Swagger documentation
- Add CI (GitHub Actions) to run tests automatically on push

## Project Structure

```
src/main/java/com/example/accountrequest/
├── controller/    # REST endpoints
├── service/       # Business logic, state machine, authorization
├── repository/    # Spring Data JPA interfaces
├── entity/        # JPA entities
├── dto/           # Request/response objects
└── exception/     # Custom exceptions + global exception handler
```
