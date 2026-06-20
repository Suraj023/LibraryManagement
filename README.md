# Library Management System

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen?style=flat-square&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.x-black?style=flat-square&logo=apachekafka)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=flat-square&logo=docker)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Minikube-326CE5?style=flat-square&logo=kubernetes)
![CI](https://img.shields.io/github/actions/workflow/status/Suraj023/LibraryManagement/ci.yml?style=flat-square&label=CI)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

A production-ready **RESTful Library Management System** built with Spring Boot 3, featuring JWT-based authentication, role-based access control, async audit logging via Apache Kafka, a custom API Gateway with rate limiting, full Docker support, and Kubernetes manifests for local cluster deployment.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Running with Docker Compose](#running-with-docker-compose)
- [Running with Kubernetes](#running-with-kubernetes)
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
- **Docker Compose** — one command spins up app + MySQL + Kafka + Zookeeper
- **Kubernetes Ready** — manifests for Deployment, Service, ConfigMap, Secret, and MySQL StatefulSet
- **GitHub Actions CI/CD** — automated build, test, and Docker image validation on every push
- **Swagger / OpenAPI 3** — interactive API docs with JWT authorization support
- **Auto DDL** — Hibernate creates/updates schema automatically on startup

---

## Architecture

```
                        ┌─────────────────────────────────────────────┐
                        │            Spring Boot Application          │
                        │                                             │
  HTTP Request          │  ┌─────────────┐      ┌─────────────────┐   │
─────────────────────►  │  │ API Gateway │─────►│  JWT Filter     │   │
                        │  │ (Rate Limit)│      │(Spring Security)│   │
                        │  └─────────────┘      └────────┬────────┘   │
                        │                                │            │
                        │                    ┌───────────▼──────────┐ │
                        │                    │      Controllers     │ │
                        │                    │  /api/books          │ │
                        │                    │  /api/users          │ │
                        │                    └───────────┬──────────┘ │
                        │                                │            │
                        │              ┌─────────────────▼──────────┐ │
                        │              │        Service Layer       │ │
                        │              └──────┬─────────────────────┘ │
                        │                     │                       │
                        │         ┌───────────▼───┐   ┌────────────┐  │
                        │         │ JPA/Hibernate │   │ Audit Log  │  │
                        │         └───────┬───────┘   │  Producer  │  │
                        │                 │           └─────┬──────┘  │
                        └─────────────────┼─────────────────┼─────────┘
                                          │                 │
                               ┌──────────▼──────┐   ┌──────▼──────────┐
                               │  MySQL Database │   │  Apache Kafka   │
                               │  (library_db)   │   │  (audit-logs)   │
                               └─────────────────┘   └──────┬──────────┘
                                                             │
                                                    ┌────────▼────────┐
                                                    │  Audit Consumer │
                                                    │  → Saves to DB  │
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
| Orchestration | Kubernetes (Minikube for local) |
| CI/CD | GitHub Actions |
| Utilities | Lombok, Joda-Time |

---

## Project Structure

```
├── src/
│   ├── main/
│   │   ├── java/com/library/
│   │   │   ├── config/
│   │   │   │   ├── JwtAuthenticationFilter.java   # JWT validation per request
│   │   │   │   ├── JwtService.java                # Token generation & parsing
│   │   │   │   ├── OpenApiConfig.java             # Swagger configuration
│   │   │   │   ├── SecurityConfig.java            # Security filter chain & rules
│   │   │   │   └── UserConfig.java                # User bean configuration
│   │   │   ├── controller/
│   │   │   │   ├── BookController.java            # Book CRUD endpoints
│   │   │   │   └── UserController.java            # User CRUD endpoints
│   │   │   ├── gateway/
│   │   │   │   └── RouteDefinition.java           # API Gateway route model
│   │   │   ├── helper/
│   │   │   │   └── DateUtils.java
│   │   │   ├── kafka/
│   │   │   │   ├── consumer/AuditLogConsumer.java # Consumes & persists audit events
│   │   │   │   └── producer/AuditLogProducer.java # Publishes audit events
│   │   │   ├── model/
│   │   │   │   ├── AuditLog.java
│   │   │   │   ├── Book.java
│   │   │   │   └── User.java
│   │   │   ├── repository/
│   │   │   │   ├── AuditLogRepository.java
│   │   │   │   ├── BookRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   └── service/
│   │   │       ├── AuditLogService.java
│   │   │       ├── BookService.java
│   │   │       ├── UserDetailsServiceImpl.java
│   │   │       └── UserService.java
│   │   └── resources/
│   │       └── application.properties             # Env-var driven config
│   └── test/
│       └── java/com/library/
│           └── LibraryApplicationTests.java
├── k8s/                                           # Kubernetes manifests
│   ├── configmap.yaml                             # Non-sensitive config
│   ├── secret.yaml                                # DB password, JWT secret
│   ├── mysql-statefulset.yaml                     # MySQL StatefulSet + PVC
│   ├── deployment.yaml                            # Spring Boot app Deployment
│   └── service.yaml                               # NodePort Service
├── Dockerfile                                     # Multi-stage build
├── docker-compose.yml                             # Full local stack
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
| Docker | 24+ |
| Minikube | latest (for Kubernetes) |

### 1. Clone the Repository

```bash
git clone https://github.com/Suraj023/LibraryManagement.git
cd LibraryManagement
```

### 2. Set Up MySQL

```sql
CREATE DATABASE library_db;
```

### 3. Start Kafka (local binary)

```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka broker
bin/kafka-server-start.sh config/server.properties
```

### 4. Build and Run

```bash
./mvnw clean package -DskipTests
java -jar target/Library-0.0.1-SNAPSHOT.jar
```

The application starts on **`http://localhost:8080`**

---

## Running with Docker Compose

The easiest way to run the full stack locally — app, MySQL, Kafka, and Zookeeper all start together with a single command.

```bash
docker-compose up --build
```

What gets started:

| Container | Port |
|-----------|------|
| `library-app` | `8080` |
| `library-mysql` | `3306` |
| `library-kafka` | `9092` (inter-container), `29092` (host) |
| `library-zookeeper` | internal only |

The app waits for MySQL's healthcheck to pass before starting, so startup order is handled automatically.

To stop and remove all containers:

```bash
docker-compose down
```

To also remove the MySQL data volume:

```bash
docker-compose down -v
```

---

## Running with Kubernetes

Kubernetes manifests live in the `k8s/` folder. These are designed for **Minikube** (local cluster).

### Prerequisites

```bash
# Install Minikube
# https://minikube.sigs.k8s.io/docs/start/

minikube start
```

### 1. Build the image into Minikube's Docker daemon

```bash
# Point your shell's Docker CLI at Minikube's daemon
eval $(minikube docker-env)

# Build the image (Minikube can now see it without a registry)
docker build -t library-management:latest .
```

### 2. Apply manifests

Apply in this order — Secret and ConfigMap must exist before the Deployment reads them.

```bash
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/mysql-statefulset.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

### 3. Verify everything is running

```bash
# Watch pods come up (Ctrl+C when all are Running)
kubectl get pods -w

# Check the MySQL StatefulSet's PersistentVolumeClaim
kubectl get pvc
```

### 4. Open the app

```bash
minikube service library-app
```

This prints and opens the correct `http://<minikube-ip>:30080` URL automatically.

### Manifest overview

| File | What it does |
|------|-------------|
| `configmap.yaml` | Non-sensitive env vars: `DB_HOST`, `DB_NAME`, `KAFKA_BOOTSTRAP_SERVERS`, etc. |
| `secret.yaml` | Sensitive values: `DB_PASSWORD`, `JWT_SECRET` (stored as K8s Secret) |
| `mysql-statefulset.yaml` | MySQL 8.0 as a StatefulSet with a 1Gi PersistentVolumeClaim |
| `deployment.yaml` | Spring Boot app with liveness/readiness probes |
| `service.yaml` | NodePort on `30080` — change to `ClusterIP` when adding Ingress |

> **Note:** Kafka is not included in these manifests yet. The ConfigMap disables Kafka consumers on startup (`SPRING_KAFKA_LISTENER_AUTO_STARTUP=false`) so the app starts cleanly. Kafka will be added via Helm in a future phase.

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

All configuration is environment-variable driven. The app has sensible local defaults so it runs without any env vars set.

| Environment Variable | Default | Description |
|----------------------|---------|-------------|
| `DB_HOST` | `127.0.0.1` | MySQL hostname |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `library_db` | Database name |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `root` | Database password |
| `JWT_SECRET` | *(base64 key)* | JWT signing secret (Base64-encoded) |
| `JWT_EXPIRY_MINUTES` | `60` | Token validity in minutes |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker address |
| `gateway.routes.users.rateLimit` | `30` | Max requests/min for `/api/users` |
| `gateway.routes.books.rateLimit` | `60` | Max requests/min for `/api/books` |

In Docker Compose, these are set in `docker-compose.yml`.
In Kubernetes, non-sensitive values come from `k8s/configmap.yaml` and secrets from `k8s/secret.yaml`.

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
- [x] Docker Compose (app + MySQL + Kafka + Zookeeper)
- [x] GitHub Actions CI/CD
- [x] Swagger / OpenAPI 3 documentation
- [x] Kubernetes manifests (Minikube — Deployment, Service, ConfigMap, Secret, StatefulSet)
- [ ] Kafka on Kubernetes via Helm (Bitnami)
- [ ] Ingress controller replacing NodePort
- [ ] Horizontal Pod Autoscaler
- [ ] Cloud deployment — GKE / EKS / AKS
- [ ] CI/CD pipeline deploys to cloud cluster (GitOps / ArgoCD)
- [ ] Observability — Prometheus + Grafana
- [ ] Distributed tracing with Micrometer
- [ ] Refresh token support

---

## Author

**Suraj Kumar Verma**

[![GitHub](https://img.shields.io/badge/GitHub-Suraj023-181717?style=flat-square&logo=github)](https://github.com/Suraj023)
[![Email](https://img.shields.io/badge/Email-s.k.verma.dev%40gmail.com-D14836?style=flat-square&logo=gmail)](mailto:s.k.verma.dev@gmail.com)

---

> Built with Spring Boot 3 · Containerized with Docker · Orchestrated with Kubernetes