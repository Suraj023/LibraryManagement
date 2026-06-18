# Library Management System

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen?style=flat-square&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.x-black?style=flat-square&logo=apachekafka)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=flat-square&logo=docker)
![CI](https://img.shields.io/github/actions/workflow/status/Suraj023/LibraryManagement/ci.yml?style=flat-square&label=CI)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

A production-ready **RESTful Library Management System** built with Spring Boot 3, featuring JWT-based authentication, role-based access control, async audit logging via Apache Kafka, a custom API Gateway with rate limiting, and full Docker support.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Running with Docker](#running-with-docker)
- [API Reference](#api-reference)
- [Swagger UI](#swagger-ui)
- [Configuration](#configuration)
- [CI/CD Pipeline](#cicd-pipeline)
- [Roadmap](#roadmap)
- [Author](#author)

---

## Features

- **JWT Authentication** — stateless token-based auth with configurable expiry
- **Role-Based Access Control** — users carry roles stored in a joined table
- **Custom API Gateway** — centralized routing with per-route rate limiting (Bucket4j)
- **Async Audit Logging** — every API call is published to a Kafka topic and persisted to the database
- **Dockerized** — multi-stage Dockerfile keeps the final image lean (JRE only)
- **GitHub Actions CI/CD** — automated build, test, and Docker image validation on every push
- **Swagger / OpenAPI 3** — interactive API docs with JWT authorization support
- **Auto DDL** — Hibernate creates/updates schema automatically on startup

---

## Architecture

```
                        ┌─────────────────────────────────────────────┐
                        │            Spring Boot Application           │
                        │                                             │
  HTTP Request          │  ┌─────────────┐      ┌─────────────────┐  │
─────────────────────►  │  │ API Gateway │─────►│  JWT Filter     │  │
                        │  │ (Rate Limit)│      │ (Spring Security)│  │
                        │  └─────────────┘      └────────┬────────┘  │
                        │                                │            │
                        │                    ┌───────────▼──────────┐ │
                        │                    │      Controllers      │ │
                        │                    │  /api/books           │ │
                        │                    │  /api/users           │ │
                        │                    └───────────┬──────────┘ │
                        │                                │            │
                        │              ┌─────────────────▼──────────┐ │
                        │              │        Service Layer        │ │
                        │              └──────┬──────────────────────┘ │
                        │                     │                        │
                        │         ┌───────────▼───┐   ┌────────────┐  │
                        │         │  JPA/Hibernate │   │ Audit Log  │  │
                        │         └───────┬───────┘   │  Producer  │  │
                        │                 │            └─────┬──────┘  │
                        └─────────────────┼──────────────────┼─────────┘
                                          │                  │
                               ┌──────────▼──────┐   ┌──────▼──────────┐
                               │   MySQL Database │   │  Apache Kafka   │
                               │   (library_db)   │   │  (audit-logs)   │
                               └─────────────────┘   └──────┬──────────┘
                                                             │
                                                    ┌────────▼────────┐
                                                    │  Audit Consumer  │
                                                    │  → Saves to DB   │
                                                    └─────────────────┘
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.4 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA / Hibernate |
| Messaging | Apache Kafka (Spring Kafka) |
| Rate Limiting | Bucket4j 7.6.0 |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Build Tool | Maven |
| Containerization | Docker (multi-stage build) |
| CI/CD | GitHub Actions |
| Utilities | Lombok, Joda-Time |

---

## Project Structure

```
src/
├── main/
│   ├── java/com/library/
│   │   ├── config/
│   │   │   ├── JwtAuthenticationFilter.java   # JWT validation per request
│   │   │   ├── JwtService.java                # Token generation & parsing
│   │   │   ├── OpenApiConfig.java             # Swagger configuration
│   │   │   ├── SecurityConfig.java            # Security filter chain & rules
│   │   │   └── UserConfig.java                # User bean configuration
│   │   ├── controller/
│   │   │   ├── BookController.java            # Book CRUD endpoints
│   │   │   └── UserController.java            # User CRUD endpoints
│   │   ├── gateway/
│   │   │   └── RouteDefinition.java           # API Gateway route model
│   │   ├── helper/
│   │   │   └── DateUtils.java
│   │   ├── kafka/
│   │   │   ├── consumer/AuditLogConsumer.java # Consumes & persists audit events
│   │   │   └── producer/AuditLogProducer.java # Publishes audit events
│   │   ├── model/
│   │   │   ├── AuditLog.java                  # Audit log entity
│   │   │   ├── Book.java                      # Book entity
│   │   │   └── User.java                      # User entity with roles
│   │   ├── repository/
│   │   │   ├── AuditLogRepository.java
│   │   │   ├── BookRepository.java
│   │   │   └── UserRepository.java
│   │   └── service/
│   │       ├── AuditLogService.java
│   │       ├── BookService.java
│   │       ├── UserDetailsServiceImpl.java
│   │       └── UserService.java
│   └── resources/
│       └── application.properties
├── test/
│   └── java/com/library/
│       └── LibraryApplicationTests.java
├── Dockerfile
├── .github/workflows/ci.yml
└── pom.xml
```

---

## Getting Started

### Prerequisites

| Tool | Version |
|------|---------|
| Java | 17+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |
| Apache Kafka | 3.x |
| Docker | 24+ (optional) |

### 1. Clone the Repository

```bash
git clone https://github.com/Suraj023/LibraryManagement.git
cd LibraryManagement
```

### 2. Set Up MySQL

```sql
CREATE DATABASE library_db;
```

### 3. Start Kafka (local)

```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka broker
bin/kafka-server-start.sh config/server.properties
```

### 4. Configure Environment

Set the following environment variables or update `application.properties`:

```bash
export DB_URL=jdbc:mysql://localhost:3306/library_db
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
export JWT_SECRET=yourBase64EncodedSecret
```

### 5. Build and Run

```bash
./mvnw clean package -DskipTests
java -jar target/Library-0.0.1-SNAPSHOT.jar
```

The application starts on **`http://localhost:8080`**

---

## Running with Docker

The project uses a **multi-stage Dockerfile** — the build stage compiles with Maven and the runtime stage uses a lean JRE image.

### Build the Image

```bash
docker build -t library-management:latest .
```

### Run with Docker

```bash
docker run -d \
  --name library-app \
  -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/library_db \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=yourpassword \
  -e JWT_SECRET=yourBase64EncodedSecret \
  library-management:latest
```

### Run Full Stack with Docker Compose

Create a `docker-compose.yml` at the project root:

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:mysql://mysql:3306/library_db
      DB_USERNAME: root
      DB_PASSWORD: root
      JWT_SECRET: U3ByaW5nQm9vdExpYnJhcnlNYW5hZ2VtZW50SldUU2VjcmV0S2V5MTIzNDU2Nzg5MA==
    depends_on:
      - mysql

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: library_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

```bash
docker-compose up -d
```

---

## API Reference

### Users — `/api/users`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/users` | Get all users | Required |
| `GET` | `/api/users/{id}` | Get user by ID | Required |
| `POST` | `/api/users` | Create new user | Public |
| `PUT` | `/api/users/update` | Update user | Required |
| `DELETE` | `/api/users/{id}` | Delete user | Required |

### Books — `/api/books`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/books` | Get all books | Public |
| `GET` | `/api/books/{id}` | Get book by ID | Public |
| `POST` | `/api/books` | Add a new book | Required |
| `DELETE` | `/api/books/{id}` | Delete a book | Required |

### Example Requests

**Create a User**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "john_doe",
    "email": "john@example.com",
    "password": "securepassword",
    "roles": ["ROLE_USER"]
  }'
```

**Add a Book (with JWT)**
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "isbn": "978-0132350884"
  }'
```

---

## Swagger UI

Interactive API documentation is available once the application is running:

| URL | Description |
|-----|-------------|
| `http://localhost:8080/swagger-ui.html` | Swagger UI |
| `http://localhost:8080/v3/api-docs` | OpenAPI JSON spec |

**How to authorize in Swagger UI:**
1. Open `http://localhost:8080/swagger-ui.html`
2. Click the **Authorize** button (lock icon)
3. Enter: `Bearer <your-jwt-token>`
4. All subsequent requests will include the token automatically

---

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:mysql://127.0.0.1:3306/library_db` | Database JDBC URL |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `root` | Database password |
| `JWT_SECRET` | *(base64 key)* | JWT signing secret |
| `b2b.app.jwtExpiryMinutes` | `60` | Token validity in minutes |
| `gateway.routes.users.rateLimit` | `30` | Max requests/min for `/api/users` |
| `gateway.routes.books.rateLimit` | `60` | Max requests/min for `/api/books` |
| `KAFKA_BROKERS` | `localhost:9092` | Kafka bootstrap servers |

---

## CI/CD Pipeline

GitHub Actions runs automatically on every push and pull request to `main`.

```
Push to main
     │
     ▼
┌──────────────────┐
│  build-and-test  │   ← Spins up MySQL 8.0 service
│  JDK 17 setup   │   ← Runs ./mvnw clean verify
│  Run all tests   │
└────────┬─────────┘
         │ success
         ▼
┌──────────────────┐
│  docker-build    │   ← Builds Docker image
│  Validates image │   ← Ensures Dockerfile is valid
└──────────────────┘
```

Pipeline file: [`.github/workflows/ci.yml`](.github/workflows/ci.yml)

---

## Roadmap

- [x] JWT Authentication & Spring Security
- [x] Book and User CRUD APIs
- [x] Async audit logging with Apache Kafka
- [x] Custom API Gateway with rate limiting
- [x] Docker multi-stage build
- [x] GitHub Actions CI/CD
- [x] Swagger / OpenAPI 3 documentation
- [ ] AWS Cloud deployment (EC2 + RDS)
- [ ] Managed Kafka via AWS MSK
- [ ] VPC with public/private subnet isolation
- [ ] Kubernetes (EKS) deployment
- [ ] Distributed tracing with Spring Actuator + Micrometer
- [ ] Refresh token support

---

## Author

**Suraj Kumar Verma**

[![GitHub](https://img.shields.io/badge/GitHub-Suraj023-181717?style=flat-square&logo=github)](https://github.com/Suraj023)
[![Email](https://img.shields.io/badge/Email-s.k.verma.dev%40gmail.com-D14836?style=flat-square&logo=gmail)](mailto:s.k.verma.dev@gmail.com)

---

> Built with Spring Boot 3 · Designed for production · Ready for the cloud
