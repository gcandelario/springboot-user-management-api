
![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-3.9-red?logo=apachemaven)
![License](https://img.shields.io/badge/license-MIT-green)

# Spring Boot User Management API

A production-ready REST API for managing user accounts built with Java and Spring Boot.

## Features

- Create, update, delete and list users
- Pagination and sorting support
- Email uniqueness validation
- Exception handling with proper HTTP status codes
- Swagger/OpenAPI documentation
- Docker support for PostgreSQL

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Docker
- Maven
- Swagger / OpenAPI
---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL 16 (H2 for tests) |
| Build | Maven 3.9 |
| Boilerplate | Lombok |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Testing | JUnit 5, Mockito, MockMvc |

---

## Project Structure

```
src/
└── main/java/com/george/usermanagementapi/
    ├── config/          # OpenAPI / Swagger configuration
    ├── controller/      # REST controllers (@RestController)
    ├── dto/
    │   ├── request/     # CreateUserRequest, UpdateUserRequest, PatchUserRequest
    │   └── response/    # UserResponse
    ├── entity/          # JPA entities
    ├── exception/       # Custom exceptions + GlobalExceptionHandler
    ├── mapper/          # Entity ↔ DTO conversion (UserMapper)
    ├── repository/      # Spring Data JPA repositories
    └── service/
        ├── UserService.java          # Interface
        └── impl/UserServiceImpl.java # Implementation
```

---

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose (for the database)

### 1 — Start PostgreSQL

```bash
docker compose up -d
```

### 2 — Configure environment

```bash
cp .env.example .env
# Edit .env if you need non-default credentials
```

### 3 — Run the application

```bash
# Export environment variables, then start with Maven
export $(cat .env | xargs)
./mvnw spring-boot:run
```

The API will be available at **http://localhost:8080**.

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | `localhost` | PostgreSQL hostname |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `usermanagement` | Database name |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `postgres` | Database password |

---

## API Endpoints

| Method | Path | Description | Status |
|--------|------|-------------|--------|
| `POST` | `/api/v1/users` | Create a user | 201 / 400 / 409 |
| `GET` | `/api/v1/users` | List users (paginated) | 200 |
| `GET` | `/api/v1/users/{id}` | Get user by ID | 200 / 404 |
| `PUT` | `/api/v1/users/{id}` | Full update | 200 / 400 / 404 / 409 |
| `PATCH` | `/api/v1/users/{id}` | Partial update | 200 / 400 / 404 / 409 |
| `DELETE` | `/api/v1/users/{id}` | Delete user | 204 / 404 |

### Pagination & Sorting

```
GET /api/v1/users?page=0&size=10&sort=lastName,asc
GET /api/v1/users?page=1&size=5&sort=createdAt,desc
```

---


## Project Structure

src/main/java/.../

controller
Handles REST API endpoints.

service
Contains business logic.

repository
Spring Data JPA repositories for database operations.

model
Entity classes mapped to PostgreSQL tables.

exception
Custom exception handling and error responses.

---

## Architecture

Client → REST API (Spring Boot) → Service Layer → Repository → PostgreSQL


---


## Run the Project

Clone the repository:

git clone https://github.com/gcandelario/springboot-user-management-api.git

Navigate to the project:

cd springboot-user-management-api

Start the database:

docker-compose up -d

Run the application:

mvn spring-boot:run

---

## Sample curl Requests

**Create a user**
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName":  "Doe",
    "email":     "john.doe@example.com",
    "phoneNumber": "+1-555-123-4567"
  }'
```

**Get all users (page 0, 10 per page)**
```bash
curl "http://localhost:8080/api/v1/users?page=0&size=10&sort=lastName,asc"
```

**Get user by ID**
```bash
curl http://localhost:8080/api/v1/users/1
```

**Full update (PUT)**
```bash
curl -X PUT http://localhost:8080/api/v1/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName":  "Smith",
    "email":     "jane.smith@example.com"
  }'
```

**Partial update (PATCH) — only change the phone number**
```bash
curl -X PATCH http://localhost:8080/api/v1/users/1 \
  -H "Content-Type: application/json" \
  -d '{ "phoneNumber": "+1-555-999-0000" }'
```

**Delete a user**
```bash
curl -X DELETE http://localhost:8080/api/v1/users/1
```

---

## API Endpoints

Create user

POST /api/v1/users

Example request:

{
  "firstName": "George",
  "lastName": "Candelario",
  "email": "george@example.com",
  "phoneNumber": "+1-787-000-0000"
}

Get all users

GET /api/v1/users?page=0&size=10&sort=lastName,asc

---

## API Documentation

Swagger UI available at:

http://localhost:8080/swagger-ui.html

---

## Error Response Format

All error responses follow a consistent JSON envelope:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status":    404,
  "error":     "Not Found",
  "message":   "User not found with id: 42",
  "path":      "/api/v1/users/42"
}
```

Validation errors (400) additionally include a `fieldErrors` map:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status":    400,
  "error":     "Bad Request",
  "message":   "Validation failed for one or more fields",
  "path":      "/api/v1/users",
  "fieldErrors": {
    "email":     "Email must be a valid email address",
    "firstName": "First name is required"
  }
}
```

---

## Running Tests

```bash
# Run all tests
./mvnw test

# Run only service-layer unit tests
./mvnw test -Dtest=UserServiceImplTest

# Run only controller integration tests
./mvnw test -Dtest=UserControllerTest

# Run with coverage report (target/site/jacoco/index.html)
./mvnw verify
```

Tests use the `test` Spring profile with an **H2 in-memory database** — no
running PostgreSQL instance is required.

---

## Swagger UI

Once the application is running, open:

```
http://localhost:8080/swagger-ui.html
```

The raw OpenAPI JSON spec is at:

```
http://localhost:8080/api-docs
```

---

<img width="2400" height="894" alt="image" src="https://github.com/user-attachments/assets/17a63792-9538-416a-b5ec-0ee39a81aacc" />

---

## License

MIT — see [LICENSE](LICENSE) for details.
